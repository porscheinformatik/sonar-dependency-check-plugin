package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Violation;

import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public final class DependencyCheckDecorator implements Decorator
{
    private RulesProfile rulesProfile;
    private Project project;
    private List<String> handledToKeys;
    private List<ProjectDependency> allowedProjectDependencies;
    private List<License> allowedLicenses;
    private Settings settings;
    private StringBuilder sb = new StringBuilder("");

    public DependencyCheckDecorator(RulesProfile rulesProfile, Settings settings)
    {
        this.rulesProfile = rulesProfile;
        handledToKeys = new ArrayList<String>();
        this.settings = settings;
    }

    public boolean shouldExecuteOnProject(Project project)
    {
        this.project = project;
        return "java".equals(project.getLanguage().getKey());
    }

    private List<ProjectDependency> getAllowedProjectDependencies()
    {
        List<ProjectDependency> result = Lists.newArrayList();
        
        String[] allowedDependencies = settings.getStringArray(DependencyCheckMetrics.LIBRARY_PROPERTY);

        getAllowedLicenses();
        
        for (String string : allowedDependencies)
        {
            
            ProjectDependency pd = new ProjectDependency();

            pd.setKey(settings.getString(DependencyCheckMetrics.LIBRARY_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LIBRARY_KEY_PROPERTY));
            pd.setVersionRange(settings.getString(DependencyCheckMetrics.LIBRARY_PROPERTY + "." + string
                + "." + DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY));
            pd.setLicenseName(settings.getString(DependencyCheckMetrics.LIBRARY_PROPERTY + "." + string
                + "." + DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY));

            if (pd.getKey() != null && !pd.getKey().isEmpty())
            {
                result.add(pd);
            }
        }

        return result;
    }

    private void getAllowedLicenses()
    {
        allowedLicenses = Lists.newArrayList();
        
        String[] licenses = settings.getStringArray(DependencyCheckMetrics.LICENSE_PROPERTY);
        
        for (String string : licenses)
        {
            License l = new License();

            l.setId(string);
            
            l.setTitle(settings.getString(DependencyCheckMetrics.LICENSE_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LICENSE_TITLE_PROPERTY));
            l.setDescription(settings.getString(DependencyCheckMetrics.LICENSE_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LICENSE_DESCRIPTION_PROPERTY));
            l.setUrl(settings.getString(DependencyCheckMetrics.LICENSE_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LICENSE_URL_PROPERTY));
            l.setSourceType(SourceType.valueOf(settings.getString(DependencyCheckMetrics.LICENSE_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY)));
            l.setCommercial(settings.getBoolean(DependencyCheckMetrics.LICENSE_PROPERTY + "." + string + "."
                + DependencyCheckMetrics.LICENSE_COMMERCIAL_PROPERTY));
            
            
            allowedLicenses.add(l);
        }
    }

    private void makeViolation(DecoratorContext context, Dependency d)
    {
        ActiveRule activeRule;

        if (!Utilities.dependencyInList(d, allowedProjectDependencies))
        {
            activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY);

            sb.append(d.getTo().getKey() + ","
                + "no license information" + "," + "Unlisted;");

            if (activeRule != null)
            {
                Violation v = Violation.create(activeRule, project);
                v.setMessage("Dependency: " + d.getTo().getKey() + " is not listed!");
                context.saveViolation(v);
            }

        }
        else if (!Utilities.dependencyInVersionRange(d, allowedProjectDependencies))
        {

            activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY);

            sb.append(d.getTo().getKey() + ","
                + Utilities.getLicenseName(d, allowedProjectDependencies) + "," + "Wrong Version;");

            if (activeRule != null)
            {
                Violation v = Violation.create(activeRule, project);
                v.setMessage("Dependency: " + d.getTo().getKey() + " is out of the accepted version range!");
                context.saveViolation(v);
            }
        }
        else
        {
            sb.append(d.getTo().getKey() + ","
                + Utilities.getLicenseName(d, allowedProjectDependencies) + "," + "OK;");
        }

    }

    public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context)
    {
        allowedProjectDependencies = getAllowedProjectDependencies();

        if (ResourceUtils.isRootProject(resource))
        {
            Logger log = LoggerFactory.getLogger(DependencyCheckDecorator.class);
            Set<Dependency> dependencies = context.getDependencies();

            log.warn("dependencies: " + dependencies.size());

            for (Dependency d : dependencies)
            {
                if (d.getFrom().getKey() == project.getKey() && !handledToKeys.contains(d.getTo().getKey()))
                {
                    log.warn("dependency: " + d.getTo().getKey());

                    makeViolation(context, d);

                    handledToKeys.add(d.getTo().getKey());
                }
            }
            context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, sb.toString()));
        }
    }
}
