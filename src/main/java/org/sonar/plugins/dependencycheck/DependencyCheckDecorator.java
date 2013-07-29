package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.design.Dependency;
import org.sonar.api.measures.Measure;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.Resource;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Violation;

import com.google.common.collect.Lists;

@SuppressWarnings("deprecation")
public final class DependencyCheckDecorator implements Decorator
{
    private RulesProfile rulesProfile;
    private Project project;
    private List<String> toKeys;
    private Settings settings;
    private StringBuilder sb = new StringBuilder("");

    public DependencyCheckDecorator(RulesProfile rulesProfile, Settings settings)
    {
        this.rulesProfile = rulesProfile;
        toKeys = new ArrayList<String>();
        this.settings = settings;
    }

    public boolean shouldExecuteOnProject(Project project)
    {
        this.project = project;
        return "java".equals(project.getLanguage().getKey());
    }

    public List<ProjectDependency> getProjectDependencies()
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

    public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context)
    {
        if (resource.getQualifier() == Qualifiers.PROJECT && resource.getKey() == project.getKey())
        {
            ActiveRule activeRule;

            Set<Dependency> dependencies = context.getDependencies();

            for (Dependency d : dependencies)
            {
                if (d.getFrom().getKey() == project.getKey() && !toKeys.contains(d.getTo().getKey()))
                {

                    if (!Utilities.dependencyInList(d, getProjectDependencies()))
                    {
                        activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                            DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY);

                        if (activeRule != null)
                        {
                            Violation v = Violation.create(activeRule, project);
                            v.setMessage("Dependency: " + d.getTo().getKey() + " is not listed!");
                            context.saveViolation(v);
                            sb.append(d.getTo().getKey() + ","
                                + "TODO License" + "," + "Unlisted;");
                        }

                    }
                    else if (!Utilities.dependencyInVersionRange(d, getProjectDependencies()))
                    {

                        activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                            DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY);

                        if (activeRule != null)
                        {
                            Violation v = Violation.create(activeRule, project);
                            v.setMessage("Dependency: " + d.getTo().getKey() + " is out of the accepted version range!");
                            context.saveViolation(v);
                            sb.append(d.getTo().getKey() + ","
                                + "TODO License" + "," + "Wrong Version;");
                        }
                    }
                    else
                    {
                        sb.append(d.getTo().getKey() + ","
                            + "TODO License" + "," + "OK;");
                    }

                    toKeys.add(d.getTo().getKey());
                }
            }

            context.saveMeasure(new Measure(DependencyCheckMetrics.DEPENDENCY, sb.toString()));
        }
    }
}
