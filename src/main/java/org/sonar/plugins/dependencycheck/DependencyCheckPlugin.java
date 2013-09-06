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

import java.util.ArrayList;
import java.util.List;

import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyFieldDefinition;
import org.sonar.api.resources.Qualifiers;

import com.google.common.collect.ImmutableList;

/**
 * This class is the entry point for all extensions
 */
public final class DependencyCheckPlugin extends SonarPlugin {
  /**
   * Returns the Properties and Class files needed by Sonar {@inheritDoc}
   */
  public List<?> getExtensions() {
    String category = "Dependency Check";
    String subGlobal = "Global Dependencies";
    String subProject = "Project Dependencies";
    String subLicense = "Licenses";
    String subScope = "Scope";
    ImmutableList.Builder<Object> extensions = ImmutableList.builder();

    List<PropertyFieldDefinition> libraryField = new ArrayList<PropertyFieldDefinition>();
    List<PropertyFieldDefinition> licenseField = new ArrayList<PropertyFieldDefinition>();

    // Library
    libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_KEY_PROPERTY)
        .name("Name")
        .description("Name of the allowed libraries in the form \"groupID:artifactID\"")
        .build()
        );
    libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_VERSION_PROPERTY)
        .name("Version Range")
        .description(
            "Version of the allowed libraries in the Maven Syntax - see: "
              + "http://maven.apache.org/enforcer/enforcer-rules/versionRanges.html "
              + "for further information! " + "Leaving this empty will allow every version.")
        .build()
        );
    libraryField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LIBRARY_LICENSE_PROPERTY)
        .name("License")
        .description("Enter the Name (either full name or shortage) of the License of the Dependency.")
        .build()
        );

    // Licenses
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_ID_PROPERTY)
        .name("ID")
        .description("Shortage of the License title (i.e. GPL, LGPL)")
        .build()
        );
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_TITLE_PROPERTY)
        .name("Title")
        .description("Title of the allowed license")
        .build()
        );
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_DESCRIPTION_PROPERTY)
        .name("Description")
        .description("Description of the license")
        .type(PropertyType.TEXT)
        .build()
        );
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_URL_PROPERTY)
        .name("URL")
        .description("URL for further description of the License")
        .build()
        );
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_COMMERCIAL_PROPERTY)
        .name("Commercial")
        .description("Enter whether the License is commercial")
        .type(PropertyType.BOOLEAN)
        .build()
        );
    licenseField.add(PropertyFieldDefinition.build(DependencyCheckMetrics.LICENSE_SOURCETYPE_PROPERTY)
        .name("Source Type")
        .description("The type of allowed source code")
        .type(PropertyType.SINGLE_SELECT_LIST)
        .options(SourceType.getSourceTypes())
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

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LICENSE_PROPERTY)
        .category(category)
        .subCategory(subLicense)
        .name("License")
        .description("Insert information about the allowed Licenses")
        .type(PropertyType.PROPERTY_SET)
        .fields(licenseField)
        .build()
        );

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.SCOPE_COMPILE_PROPERTY)
        .category(category)
        .subCategory(subScope)
        .name("Compile")
        .description("Whether dependencies of the scope compile should be checked.")
        .type(PropertyType.BOOLEAN)
        .defaultValue("true")
        .onQualifiers(Qualifiers.PROJECT)
        .build());

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.SCOPE_RUNTIME_PROPERTY)
        .category(category)
        .subCategory(subScope)
        .name("Runtime")
        .description("Whether dependencies of the scope runtime should be checked.")
        .type(PropertyType.BOOLEAN)
        .defaultValue("true")
        .onQualifiers(Qualifiers.PROJECT)
        .build());

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.SCOPE_TEST_PROPERTY)
        .category(category)
        .subCategory(subScope)
        .name("Test")
        .description("Whether dependencies of the scope test should be checked.")
        .type(PropertyType.BOOLEAN)
        .defaultValue("false")
        .onQualifiers(Qualifiers.PROJECT)
        .build());

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.SCOPE_PROVIDED_PROPERTY)
        .category(category)
        .subCategory(subScope)
        .name("Provided")
        .description("Whether dependencies of the scope provided should be checked.")
        .type(PropertyType.BOOLEAN)
        .defaultValue("false")
        .onQualifiers(Qualifiers.PROJECT)
        .build());

    extensions.add(DependencyCheckRuleRepository.class);
    extensions.add(DependencyCheckMetrics.class);
    extensions.add(DependencyCheckDecorator.class);
    extensions.add(DependencyCheckPage.class);

    return extensions.build();
  }
}
