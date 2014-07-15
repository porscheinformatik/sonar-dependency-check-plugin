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

import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

/**
 * Repository for the rules used in the plugin
 */
public final class DependencyCheckRulesDefinition implements RulesDefinition {

  @Override
  public void define(Context context) {
    NewRepository repository = context.createRepository(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY, "java");
    repository.setName("Dependency Check");

    repository.createRule(DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY)
        .setName("Unlisted Dependency Violation [dependency-check]")
        .setHtmlDescription("Violation because a dependency is not listed!")
        .setSeverity(Severity.BLOCKER);

    repository.createRule(DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY)
        .setName("Dependency with wrong Version Violation [dependency-check]")
        .setHtmlDescription("Violation because a dependency is out of the accepted version range!")
        .setSeverity(Severity.CRITICAL);

    repository.done();
  }
}
