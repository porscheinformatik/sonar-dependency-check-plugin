package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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

import com.google.common.collect.Lists;

/**
 * This class creates Issues and Measures for the analyzed project.
 */
public final class DependencyCheckDecorator implements Decorator
{
    private final Settings settings;
    private final ResourcePerspectives perspectives;

    /**
     * Dependency Injection of settings and perspectives
     * 
     * @param settings - settings for the plugin (contains the properties)
     * @param perspectives - needed for creating issues
     */
    public DependencyCheckDecorator(Settings settings, ResourcePerspectives perspectives)
    {
        this.settings = settings;
        this.perspectives = perspectives;
    }

    /**
     * {@inheritDoc}
     */
    public boolean shouldExecuteOnProject(Project project)
    {
        return "java".equals(project.getLanguage().getKey());
    }

    /**
     * Creates a List of allowed Dependencies for the Project - configurable in the Project settings in the category
     * java
     * 
     * @return List of allowed dependencies
     */
    private List<ProjectDependency> getAllowedProjectDependencies()
    {
        List<ProjectDependency> result = Lists.newArrayList();

        String[] allowedDependencies = settings.getStringArray(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY);

        List<License> allowedLicenses = getAllowedLicenses();

        for (String string : allowedDependencies)
        {

            ProjectDependency pd = new ProjectDependency();

            pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
            pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string
                + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
            pd.setLicense(Utilities.getLicenseByName(
                settings.getString(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY + "." + string
                    + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));

            if (pd.getKey() != null && !pd.getKey().isEmpty())
            {
                result.add(pd);
            }
        }

        allowedDependencies = settings.getStringArray(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY);

        for (String string : allowedDependencies)
        {

            ProjectDependency pd = new ProjectDependency();

            pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
            pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string
                + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
            pd.setLicense(Utilities.getLicenseByName(
                settings.getString(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY + "." + string
                    + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY), allowedLicenses));

            if (pd.getKey() != null && !pd.getKey().isEmpty())
            {
                result.add(pd);
            }
        }

        return result;
    }

    /**
     * Creates a List of allowed Licenses for the Project - configurable in the licenses.properties file
     * 
     * @return
     */
    private List<License> getAllowedLicenses()
    {
        List<License> allowedLicenses = Lists.newArrayList();

        Properties licensesProps = new Properties();

        Utilities.readLicenseProperties(licensesProps);

        String[] allowed = licensesProps.getProperty("license.list").split("\\|");

        for (String s : allowed)
        {
            License l = new License();

            l.setId(s);
            String temp = "license.".concat(s);
            l.setTitle(licensesProps.getProperty(temp + ".name"));
            l.setUrl(licensesProps.getProperty(temp + ".url"));
            l.setDescription(licensesProps.getProperty(temp + ".description"));
            l.setCommercial(licensesProps.getProperty(temp + ".commercial").contains("true") ? true : false);
            l.setSourceType(SourceType.valueOf(licensesProps.getProperty(temp + ".sourcetype").isEmpty()
                ? SourceType.CLOSED.name() : licensesProps.getProperty(temp + ".sourcetype")));
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
    private void makeIssue(Project project, Dependency d, StringBuilder sbDependencies,
        StringBuilder sbLicenses, List<ProjectDependency> allowedProjectDependencies)
    {
        if (!Utilities.dependencyInList(d, allowedProjectDependencies))
        {
            sbDependencies.append(d.getTo().getKey() + ","
                + "no license information" + "," + "Unlisted;");

            Issuable issuable = perspectives.as(Issuable.class, (Resource) project);
            if (issuable != null)
            {
                Issue issue = issuable.newIssueBuilder()
                    .ruleKey(RuleKey.of(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                        DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY))
                    .message("Dependency: " + d.getTo().getKey() + " is not listed!")
                    .build();
                issuable.addIssue(issue);
            }
        }
        else if (!Utilities.dependencyInVersionRange(d, allowedProjectDependencies))
        {
            sbDependencies.append(d.getTo().getKey() + ","
                + Utilities.getLicenseName(d, allowedProjectDependencies) + "," + "Wrong Version;");

            License l = Utilities.getLicense(d, allowedProjectDependencies);

            if (!sbLicenses.toString().contains(l.getTitle() + "," + l.getUrl() + ";"))
            {
                sbLicenses.append(l.getTitle() + "," + l.getUrl() + ";");
            }

            Issuable issuable = perspectives.as(Issuable.class, (Resource) project);
            if (issuable != null)
            {
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
        else
        {
            sbDependencies.append(d.getTo().getKey() + ","
                + Utilities.getLicenseName(d, allowedProjectDependencies) + "," + "OK;");

            License l = Utilities.getLicense(d, allowedProjectDependencies);

            if (!sbLicenses.toString().contains(l.getTitle() + "," + l.getUrl() + ";"))
            {
                sbLicenses.append(l.getTitle() + "," + l.getUrl() + ";");
            }
        }
    }

    /**
     * Function called by Sonar - controls the DependencyCheck analysis {@inheritDoc}
     */
    public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context)
    {
        if (ResourceUtils.isRootProject(resource))
        {
            StringBuilder sbDependencies = new StringBuilder("");
            StringBuilder sbLicenses = new StringBuilder("");
            List<String> handledToKeys = new ArrayList<String>();

            // resource has to be a project here
            Project project = (Project) resource;

            List<ProjectDependency> allowedProjectDependencies = getAllowedProjectDependencies();

            Logger log = LoggerFactory.getLogger(DependencyCheckDecorator.class);
            Set<Dependency> dependencies = context.getDependencies();

            log.warn("dependencies: " + dependencies.size());

            for (Dependency d : dependencies)
            {
                if (d.getFrom().getKey().equals(project.getKey()) && !handledToKeys.contains(d.getTo().getKey()))
                {
                    log.warn("dependency: " + d.getTo().getKey());

                    makeIssue(project, d, sbDependencies, sbLicenses, allowedProjectDependencies);

                    handledToKeys.add(d.getTo().getKey());
                }
            }
            context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, sbDependencies.toString()));
            context.saveMeasure(new Measure(DependencyCheckMetrics.LICENSE, sbLicenses.toString()));
        }
    }
}
