package org.sonar.plugins.dependencycheck;

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

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

/**
 * This class creates Issues and Measures for the analyzed project.
 */
public final class DependencyCheckDecorator implements Decorator {
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
    List<ProjectDependency> result = newArrayList();

    String[] allowedDependencies = settings.getStringArray(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY);

    List<License> allowedLicenses = getAllowedLicenses();

    for (String string : allowedDependencies) {
      ProjectDependency pd = new ProjectDependency();

      pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string + "."
        + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
      pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string
        + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
      pd.setLicense(Utilities.getLicenseByNameOrId(
          settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string
            + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));

      if (pd.getKey() != null && !pd.getKey().isEmpty()) {
        result.add(pd);
      }
    }

    allowedDependencies = settings.getStringArray(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY);

    for (String string : allowedDependencies) {
      ProjectDependency pd = new ProjectDependency();

      pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string + "."
        + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
      pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string
        + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
      pd.setLicense(Utilities.getLicenseByNameOrId(
          settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string
            + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));

      if (pd.getKey() != null && !pd.getKey().isEmpty()) {
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
    List<License> allowedLicenses = newArrayList();

    String[] allowed = settings.getStringArray(DependencyCheckMetrics.LICENSE_PROPERTY);

    for (String s : allowed) {
      License l = new License();

      String temp = DependencyCheckMetrics.LICENSE_PROPERTY.concat("." + s + ".");

      l.setId(settings.getString(temp + DependencyCheckMetrics.LICENSE_ID_PROPERTY));
      l.setTitle(settings.getString(temp + DependencyCheckMetrics.LICENSE_TITLE_PROPERTY));
      l.setUrl(settings.getString(temp + DependencyCheckMetrics.LICENSE_URL_PROPERTY));
      l.setDescription(settings.getString(temp + DependencyCheckMetrics.LICENSE_DESCRIPTION_PROPERTY));
      l.setCommercial(settings.getString(temp + DependencyCheckMetrics.LICENSE_COMMERCIAL_PROPERTY).contains(
          "true"));

      String st = settings.getString(temp + DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY);
      if (st != null && !st.isEmpty()) {
        l.setSourceType(SourceType.valueOf(settings.getString(
            temp + DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY)));
      }
      else {
        l.setSourceType(SourceType.OPENSOURCE_COPYLEFT);
      }
      allowedLicenses.add(l);
    }

    return allowedLicenses;
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
    if (!Utilities.dependencyInList(d, allowedProjectDependencies)) {

      allDependencies.add(d.getTo().getKey() + "~" + "no license information" + "~" + "Unlisted");

      Issuable issuable = perspectives.as(Issuable.class, (Resource) project);
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

      allDependencies.add(d.getTo().getKey() + "~" + Utilities.getLicenseName(d, allowedProjectDependencies) + "~" + "Wrong Version");

      License l = Utilities.getLicense(d, allowedProjectDependencies);
      allLicenses.add(l.getTitle() + "~" + l.getUrl());

      Issuable issuable = perspectives.as(Issuable.class, (Resource) project);
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
    if (!ResourceUtils.isProject(resource)) {
      return;
    }

    SortedSet<String> lincenseAnalysisResult = newTreeSet();
    SortedSet<String> dependencyAnalysisResult = newTreeSet();
    Set<String> handledToKeys = newHashSet();

    // resource has to be a project here
    Project project = (Project) resource;

    List<ProjectDependency> allowedProjectDependencies = getAllowedProjectDependencies();
    List<String> allowedScopes = getAllowedScopes();

    Set<Dependency> dependencies = context.getDependencies();

    for (Dependency d : dependencies) {
      if (d.getFrom().getKey().equals(project.getKey())
        && !handledToKeys.contains(d.getTo().getKey())
        && !Utilities.hasSameRoot(d)
        && Utilities.inCheckScope(d, allowedScopes)) {

        checkDependency(project, d, dependencyAnalysisResult, lincenseAnalysisResult, allowedProjectDependencies);

        handledToKeys.add(d.getTo().getKey());
      }
    }

    Measure[] dependencyMeasures =
        context.getChildrenMeasures(DependencyCheckMetrics.DEPENDENCY).toArray(
            new Measure[context.getChildrenMeasures(DependencyCheckMetrics.DEPENDENCY).size()]);

    for (Measure measure : dependencyMeasures) {
      if (!dependencyAnalysisResult.toString().contains(measure.getData())) {
        dependencyAnalysisResult.add(measure.getData());
      }
    }

    Measure[] licenseMeasures =
        context.getChildrenMeasures(DependencyCheckMetrics.LICENSE).toArray(
            new Measure[context.getChildrenMeasures(DependencyCheckMetrics.LICENSE).size()]);

    for (Measure measure : licenseMeasures) {
      if (!lincenseAnalysisResult.toString().contains(measure.getData())) {
        lincenseAnalysisResult.add(measure.getData());
      }
    }

    context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, Utilities.concatStringList(dependencyAnalysisResult)));
    context.saveMeasure(new Measure(DependencyCheckMetrics.LICENSE, Utilities.concatStringList(lincenseAnalysisResult)));
  }

}
