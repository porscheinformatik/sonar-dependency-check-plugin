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

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_GLOBAL_PROPERTY)
        .category(category)
        .subCategory(subGlobal)
        .name("Allowed Dependencies (XML)")
        .description("Insert information about the allowed Libraries")
        .type(PropertyType.TEXT)
        .fields(libraryField)
        .build()
        );

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LIBRARY_PROJECT_PROPERTY)
        .category(category)
        .subCategory(subProject)
        .name("Allowed Dependencies (XML)")
        .description("Insert information about the allowed Libraries")
        .type(PropertyType.TEXT)
        .fields(libraryField)
        .onlyOnQualifiers(Qualifiers.PROJECT)
        .build()
        );

    extensions.add(PropertyDefinition.builder(DependencyCheckMetrics.LICENSE_PROPERTY)
        .category(category)
        .subCategory(subLicense)
        .name("Licenses (XML)")
        .description("Insert information about the allowed Licenses")
        .type(PropertyType.TEXT)
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

    extensions.add(DependencyCheckRulesDefinition.class);
    extensions.add(DependencyCheckMetrics.class);
    extensions.add(DependencyCheckDecorator.class);
    extensions.add(DependencyCheckPage.class);

    return extensions.build();
  }
}
