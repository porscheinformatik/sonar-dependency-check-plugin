package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.CoreProperties;
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
    public List<?> getExtensions()
    {
        String subCategory = "DependencyCheck";
        ImmutableList.Builder<Object> extensions = ImmutableList.builder();

        List<PropertyFieldDefinition> licenseField = new ArrayList<PropertyFieldDefinition>();
        List<PropertyFieldDefinition> libraryField = new ArrayList<PropertyFieldDefinition>();

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
            .options("1", "2", "3")
            .build()
            );

        licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_TITLE_PROPERTY)
            .name("License Title")
            .description("The name of the license")
            .build()
            );
        licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_DESCRIPTION_PROPERTY)
            .name("License Description")
            .description("Further description of the license")
            .type(PropertyType.TEXT)
            .build()
            );
        licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_URL_PROPERTY)
            .name("URL")
            .description("Online reference for further information on the license")
            .build()
            );
        licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY)
            .name("Source Type")
            .description("The type of source code the license supports")
            .type(PropertyType.SINGLE_SELECT_LIST)
            .build()
            );
        licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_COMMERCIAL_PROPERTY)
            .name("Commercial")
            .description("Insert whether the licens is commercial or not")
            .type(PropertyType.BOOLEAN)
            .build()
            );

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_PROPERTY)
            .category(CoreProperties.CATEGORY_JAVA)
            .subCategory(subCategory)
            .name("Library")
            .description("Insert information about the allowed Libraries")
            .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
            .type(PropertyType.PROPERTY_SET)
            .fields(libraryField)
            .build()
            );

        extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LICENSE_PROPERTY)
            .category(CoreProperties.CATEGORY_JAVA)
            .subCategory(subCategory)
            .name("License")
            .description("Insert information about the allowed License")
            .onQualifiers(Qualifiers.PROJECT, Qualifiers.MODULE)
            .type(PropertyType.PROPERTY_SET)
            .fields(licenseField)
            .build()
            );

        extensions.add(DependencyCheckRuleRepository.class);
        extensions.add(DependencyCheckMetrics.class);
        extensions.add(DependencyCheckDecorator.class);
        extensions.add(DependencyCheckWidget.class);

        return extensions.build();
    }

}

