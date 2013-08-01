package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyFieldDefinition;
import org.sonar.api.resources.Qualifiers;

import com.google.common.collect.ImmutableList;

/**
 * This class is the entry point for all extensions
 */
public final class DependencyCheckPlugin extends SonarPlugin
{
    /**
     * Returns the Properties and Class files needed by Sonar {@inheritDoc}
     */
    public List<?> getExtensions()
    {
        String category = "Dependency Check";
        String subGlobal = "Global Dependencies";
        String subProject = "Project Dependencies";
        ImmutableList.Builder<Object> extensions = ImmutableList.builder();

        Properties licensesProps = new Properties();

        Utilities.readLicenseProperties(licensesProps);

        String[] allowed = licensesProps.getProperty("license.list").split("\\|");

        List<PropertyFieldDefinition> libraryField = new ArrayList<PropertyFieldDefinition>();

        // Library
        libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_KEY_PROPERTY)
            .name("Dependency name")
            .description("Name of the allowed libraries in the form \"groupID:artifactID\"")
            .build()
            );
        libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY)
            .name("Dependency version")
            .description(
                "Version of the allowed libraries in the Maven Syntax - see: "
                    + "http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html "
                    + "for further information! " + "Leaving this empty will allow every version.")
            .build()
            );
        libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY)
            .name("Dependency License")
            .description("Select the License of the Dependency.")
            .type(PropertyType.SINGLE_SELECT_LIST)
            .options(allowed)
            .build()
            );

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY)
            .category(category)
            .subCategory(subGlobal)
            .name("Library")
            .description("Insert information about the allowed Libraries")
            .type(PropertyType.PROPERTY_SET)
            .fields(libraryField)
            .build()
            );

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY)
            .category(category)
            .subCategory(subProject)
            .name("Library")
            .description("Insert information about the allowed Libraries")
            .type(PropertyType.PROPERTY_SET)
            .fields(libraryField)
            .onQualifiers(Qualifiers.PROJECT)
            .build()
            );

        extensions.add(DependencyCheckRuleRepository.class);
        extensions.add(DependencyCheckMetrics.class);
        extensions.add(DependencyCheckDecorator.class);
        extensions.add(DependencyCheckWidget.class);

        return extensions.build();
    }
}
