package org.sonar.plugins.dependencycheck;

import java.util.List;

import org.sonar.api.CoreProperties;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import com.google.common.collect.ImmutableList;

/**
 * This class is the entry point for all extensions
 */
public final class DependencyCheckPlugin extends SonarPlugin
{

    public List<?> getExtensions()
    {
        String subCategory = "DependencyCheck";
        ImmutableList.Builder<Object> extensions = ImmutableList.builder();

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_KEY_PROPERTY)
            .category(CoreProperties.CATEGORY_JAVA)
            .subCategory(subCategory)
            .name("Dependency name")
            .description("Name of the allowed libraries in the form \"groupID:artifactID\"")
            .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
            .multiValues(true)
            .build()
            );

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY)
            .category(CoreProperties.CATEGORY_JAVA)
            .subCategory(subCategory)
            .name("Dependency version")
            .description(
                "Version of the allowed libraries in the Maven Syntax - see: "
                    + "http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html "
                    + "for further information! " + "Leaving this empty will allow every version.")
            .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
            .multiValues(true)
            .build()
            );

        extensions.add(DependencyCheckRuleRepository.class);
        extensions.add(DependencyCheckMetrics.class);
        extensions.add(DependencyCheckDecorator.class);
        return extensions.build();
    }
}