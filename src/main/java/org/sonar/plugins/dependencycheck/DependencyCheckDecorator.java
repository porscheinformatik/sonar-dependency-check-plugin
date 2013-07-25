package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinition.Result;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.config.PropertyFieldDefinition;
import org.sonar.api.config.Settings;
import org.sonar.api.design.Dependency;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
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
        Logger log = LoggerFactory.getLogger(DependencyCheckDecorator.class);
        List<ProjectDependency> result = Lists.newArrayList();

        PropertyDefinitions definitions = settings.getDefinitions();
        PropertyDefinition propSet = definitions.get(DependencyCheckMetrics.LIBRARY_PROPERTY);

        List<PropertyFieldDefinition> fields = new ArrayList<PropertyFieldDefinition>();

        fields.addAll(propSet.fields());

        PropertyFieldDefinition keys = fields.get(0);
        PropertyFieldDefinition versions = fields.get(1);

        String allowedDependenciesKey = keys.key();
        String allowedDependenciesVersion = versions.key();

        //log.warn("keys length: " + allowedDependenciesKey.size());
//        log.warn("vers length: " + allowedDependenciesVersion.length);
        
        log.warn("key 0: " + allowedDependenciesKey);
        log.warn("ver 0: " + allowedDependenciesVersion);

        log.warn("data key 0: " + settings.getString(allowedDependenciesKey));
        log.warn("data ver 0: " + settings.getString(allowedDependenciesVersion));
        
        //        
        //        String[] allowedDependenciesKey = settings.getStringLines(DependencyCheckMetrics.LIBRARY_KEY_PROPERTY);
        //        String[] allowedDependenciesVersion = settings.getStringLines(DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY);
        //
        //        for (int i = 0; i < allowedDependenciesKey.length; i++)
        //        {
        //            if (StringUtils.isNotBlank(allowedDependenciesKey[i]))
        //            {
        //                String tempVersion;
        //                tempVersion =
        //                    allowedDependenciesVersion.length > i && allowedDependenciesVersion[i] != null
        //                        ? allowedDependenciesVersion[i] : "";
        //                result.add(new ProjectDependency(allowedDependenciesKey[i], tempVersion));
        //            }
        //        }
        return result;
    }

    public void decorate(@SuppressWarnings("rawtypes") Resource resource, DecoratorContext context)
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

                    Violation v = Violation.create(activeRule, project);
                    v.setMessage("Dependency: " + d.getTo().getKey() + " is not listed!");
                    context.saveViolation(v);
                }
                else if (!Utilities.dependencyInVersionRange(d, getProjectDependencies()))
                {

                    activeRule = rulesProfile.getActiveRule(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
                        DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY);

                    Violation v = Violation.create(activeRule, project);
                    v.setMessage("Dependency: " + d.getTo().getKey() + " is out of the accepted version range!");
                    context.saveViolation(v);
                }

                toKeys.add(d.getTo().getKey());
            }
        }
    }
}
