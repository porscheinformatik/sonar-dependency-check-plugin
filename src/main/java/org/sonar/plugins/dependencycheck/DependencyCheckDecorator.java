package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

    public List<ProjectDependency> getAllowedProjectDependencies()
    {
        List<ProjectDependency> result = Lists.newArrayList();
        String[] allowedDependenciesKey = settings.getStringArray(DependencyCheckMetrics.LIBRARY_KEY_PROPERTY);
        String[] allowedDependenciesVersion = settings.getStringArray(DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY);

        for (int i = 0; i < allowedDependenciesKey.length; i++)
        {
            if (StringUtils.isNotBlank(allowedDependenciesKey[i]))
            {
                String tempVersion;
                tempVersion =
                    allowedDependenciesVersion.length > i && allowedDependenciesVersion[i] != null
                        ? allowedDependenciesVersion[i] : "";
                result.add(new ProjectDependency(allowedDependenciesKey[i], tempVersion));
            }
        }
        return result;
    }

    private void makeViolation(DecoratorContext context, Dependency d)
    {
        ActiveRule activeRule;
        
        if (!Utilities.dependencyInList(d, allowedProjectDependencies))
        {
            activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY);


            sb.append(d.getTo().getKey() + ","
                + "TODO License" + "," + "Unlisted;");
            
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
                + "TODO License" + "," + "Wrong Version;");
            
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
                + "TODO License" + "," + "OK;");
        }

    }

    public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context)
    {
        allowedProjectDependencies = getAllowedProjectDependencies();
        
        if(ResourceUtils.isRootProject(resource))
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

            //sb.append("com.puppycrawl.tools:checkstyle,TODO License,Unlisted;");
            context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, sb.toString()));
        }
    }
}
