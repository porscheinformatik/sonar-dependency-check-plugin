/*
 * Sonar Dependency Check Plugin
 * Copyright (C) 2013 Porsche Informatik
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.dependencycheck;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static com.google.common.collect.Sets.newTreeSet;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.io.StringReader;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.design.Dependency;
import org.sonar.api.issue.Issuable;
import org.sonar.api.issue.Issue;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Library;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rule.RuleKey;


import javax.xml.bind.JAXBException;

/**
 * This class creates Issues and Measures for the analyzed project.
 */
public final class DependencyCheckDecorator implements Decorator {
  private static final Logger LOGGER = LoggerFactory.getLogger(DependencyCheckDecorator.class);
  private final Settings settings;
  private final ResourcePerspectives perspectives;

  /**
   * Dependency Injection of settings and perspectives
   *
   * @param settings - settings for the plugin (contains the properties)
   * @param perspectives - needed for creating issues
   */
  public DependencyCheckDecorator(Settings settings, ResourcePerspectives perspectives) {
    this.settings = settings;
    this.perspectives = perspectives;
  }

  /**
   * {@inheritDoc}
   */
  public boolean shouldExecuteOnProject(Project project) {
    return true;
  }

  /**
   * Creates a List of allowed Dependencies for the Project - configurable in the Project settings in the category
   * dependency check
   *
   * @return List of allowed dependencies
   */
  private List<ProjectDependency> getAllowedProjectDependencies() {
    
      List<License> allowedLicenses = getAllowedLicenses();
      List<ProjectDependency> result = newArrayList();
     
      DependencyWrapper allowedPds=null;
      
      try{
          allowedPds= JaxbXmlHandler.unmarshalAllowedProjectDependencies(new StringReader(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY)));     
      }
      catch (JAXBException e){
          e.printStackTrace();
      }
      for(ProjectDependency pd:allowedPds.getList()){
          
          pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + pd.getKey() + "." + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
          pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + pd.getVersionRange() + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
          pd.setLicense(Utilities.getLicenseByNameOrId(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + pd.getLicense() + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));
          
          if (isNotEmpty(pd.getKey())) {
              result.add(pd);
            }   
      }

    DependencyWrapper allowedGlobalPds=null;
    
    try{
        allowedGlobalPds= JaxbXmlHandler.unmarshalAllowedProjectDependencies(new StringReader(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY))); 
    }
    catch (JAXBException e){
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
    for(ProjectDependency pd:allowedGlobalPds.getList()){
        
        pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + pd.getKey() + "." + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
        pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + pd.getVersionRange() + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
        pd.setLicense(Utilities.getLicenseByNameOrId(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + pd.getLicense() + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));
        
        if (isNotEmpty(pd.getKey())) {
            result.add(pd);
          }
    }
    
    return result;
  }

  private List<String> getAllowedScopes() {

    List<String> allowedScopes = newArrayList();

    if (settings.getBoolean(DependencyCheckMetrics.SCOPE_COMPILE_PROPERTY)) {
      allowedScopes.add("compile");
    }
    if (settings.getBoolean(DependencyCheckMetrics.SCOPE_RUNTIME_PROPERTY)) {
      allowedScopes.add("runtime");
    }
    if (settings.getBoolean(DependencyCheckMetrics.SCOPE_TEST_PROPERTY)) {
      allowedScopes.add("test");
    }
    if (settings.getBoolean(DependencyCheckMetrics.SCOPE_PROVIDED_PROPERTY)) {
      allowedScopes.add("provided");
    }

    return allowedScopes;
  }

  /**
   * @return a List of allowed Licenses for the Project - configurable in settings
   */
  private List<License> getAllowedLicenses() {
      
      LicenseWrapper allowedL=null;
      
      try{
          allowedL= JaxbXmlHandler.unmarshalLicenseWrapper(new StringReader(settings.getString(DependencyCheckMetrics.LICENSE_PROPERTY))); 
      }
      catch (JAXBException e){
          e.printStackTrace();
      }
      for (License l : allowedL.getList())
    {
        l.setId(settings.getString("." + l.getId() + "." + DependencyCheckMetrics.LICENSE_ID_PROPERTY));
        l.setTitle(settings.getString("." + l.getTitle() + "." + DependencyCheckMetrics.LICENSE_TITLE_PROPERTY));
        l.setUrl(settings.getString("." + l.getUrl() + "." + DependencyCheckMetrics.LICENSE_URL_PROPERTY));
        l.setDescription(settings.getString("." + l.getDescription() + "." + DependencyCheckMetrics.LICENSE_DESCRIPTION_PROPERTY));
        l.setCommercial(settings.getBoolean("." + l.isCommercial() + "." + DependencyCheckMetrics.LICENSE_COMMERCIAL_PROPERTY));
        
        String sourceType= settings.getString("." + l.getSourceType() + DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY);
        if(isNotEmpty(sourceType)) {
            
            l.setSourceType(SourceType.valueOf(sourceType));
        }
        else {
            l.setSourceType(SourceType.OPENSOURCE_COPYLEFT);
          }
    }
      
      return allowedL.getList();
  }

  /**
   * Creates Issues if rules are violated and appends information about the used dependencies and licenses on 2
   * StringBuilders
   *
   * @param project - the current Project
   * @param context
   * @param d - currently handled dependency
   * @param sbDependencies - StringBuffer for dependencies
   * @param sbLicenses - StringBuffer for licenses
   * @param allowedProjectDependencies - list of the allowed dependencies
   */
  private void checkDependency(Project project, Dependency d, Set<String> allDependencies,
      Set<String> allLicenses, List<ProjectDependency> allowedProjectDependencies) {

    LOGGER.debug("Checking dependency: {}", d);

    if (!Utilities.dependencyInList(d, allowedProjectDependencies)) {

      allDependencies.add(d.getTo().getKey() + "~" + "no license information" + "~" + "UNLISTED");

      Issuable issuable = perspectives.as(Issuable.class, (Resource<?>) project);
      if (issuable != null) {
        Issue issue = issuable.newIssueBuilder()
            .ruleKey(RuleKey.of(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY))
            .message("Dependency: " + d.getTo().getKey() + " is not listed!")
            .build();
        issuable.addIssue(issue);
      }

    }
    else if (!Utilities.dependencyInVersionRange(d, allowedProjectDependencies)) {

      allDependencies.add(d.getTo().getKey() + "~" + Utilities.getLicenseName(d, allowedProjectDependencies) + "~" + "WRONG_VERSION");

      License l = Utilities.getLicense(d, allowedProjectDependencies);
      allLicenses.add(l.getTitle() + "~" + l.getUrl());

      Issuable issuable = perspectives.as(Issuable.class, (Resource<?>) project);
      if (issuable != null) {
        Issue issue =
            issuable
                .newIssueBuilder()
                .ruleKey(RuleKey.of(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                    DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY))
                .message(
                    "Dependency: " + d.getTo().getKey() + " with version: "
                      + ((Library) d.getTo()).getVersion()
                      + " is out of the accepted version range! Accepted version Range: "
                      + Utilities.getDependencyVersionRange(d, allowedProjectDependencies))
                .build();
        issuable.addIssue(issue);
      }
    }
    else {

      allDependencies.add(d.getTo().getKey() + "~" + Utilities.getLicenseName(d, allowedProjectDependencies) + "~" + "OK");

      License l = Utilities.getLicense(d, allowedProjectDependencies);

      allLicenses.add(l.getTitle() + "~" + l.getUrl());
    }
  }

  /**
   * {@inheritDoc}
   */
  public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context) {
    if (!(ResourceUtils.isProject(resource) || ResourceUtils.isModuleProject(resource))) {
      return;
    }

    SortedSet<String> lincenseAnalysisResult = newTreeSet();
    SortedSet<String> dependencyAnalysisResult = newTreeSet();
    Set<String> handledToKeys = newHashSet();

    // resource has to be a project here
    Project project = (Project) resource;

    LOGGER.debug("Dependency check for project: {}", project);

    List<ProjectDependency> allowedProjectDependencies = getAllowedProjectDependencies();
    List<String> allowedScopes = getAllowedScopes();

    Collection<Dependency> dependencies = context.getDependencies();
    LOGGER.debug("Got dependencies: {}", dependencies);

    for (Dependency d : dependencies) {
        if (ResourceUtils.isLibrary(d.getTo()) // only include libraries
            && !handledToKeys.contains(d.getTo().getKey())
            && Utilities.inCheckScope(d, allowedScopes)) {
            
            checkDependency(project, d, dependencyAnalysisResult, lincenseAnalysisResult, allowedProjectDependencies);
            
            handledToKeys.add(d.getTo().getKey());
        }
    }

    saveProjectMeasures(context, lincenseAnalysisResult, dependencyAnalysisResult);
  }

  /**
   * Saves the dependencies and licenses to project measures for display in UI.
   * @param context .
   * @param lincenseAnalysisResult .
   * @param dependencyAnalysisResult .
   */
  private void saveProjectMeasures(DecoratorContext context, SortedSet<String> lincenseAnalysisResult, SortedSet<String> dependencyAnalysisResult) {

    Collection<Measure> dependencyMeasures = context.getChildrenMeasures(DependencyCheckMetrics.DEPENDENCY);
    if (dependencyMeasures != null) {
      for (Measure measure : dependencyMeasures) {
        if (measure.getData() != null) {
          String[] subProjectDependencies = measure.getData().split(";");
          dependencyAnalysisResult.addAll(asList(subProjectDependencies));
        }
      }
    }

    Collection<Measure> licenseMeasures = context.getChildrenMeasures(DependencyCheckMetrics.LICENSE);
    if (licenseMeasures != null) {
      for (Measure measure : licenseMeasures) {
        if (measure.getData() != null) {
          String[] subProjectLicenses = measure.getData().split(";");
          lincenseAnalysisResult.addAll(asList(subProjectLicenses));
        }
      }
    }

    context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, Utilities.concatStringList(dependencyAnalysisResult)));
    context.saveMeasure(new Measure(DependencyCheckMetrics.LICENSE, Utilities.concatStringList(lincenseAnalysisResult)));
  }

}
