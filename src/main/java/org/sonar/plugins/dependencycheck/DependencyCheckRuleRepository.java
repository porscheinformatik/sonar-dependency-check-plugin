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

import java.util.Arrays;
import java.util.List;

import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleRepository;

/**
 * Repository for the rules used in the plugin
 */
public final class DependencyCheckRuleRepository extends RuleRepository {
  public static final Rule UNLISTED = Rule.create(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
      DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY,
      "Unlisted Dependency Violation [dependency-check]")
      .setDescription("Violation because a dependency is not listed!")
      .setSeverity(RulePriority.BLOCKER);

  public static final Rule WRONG_VERSION = Rule.create(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
      DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY,
      "Dependency with wrong Version Violation [dependency-check]")
      .setDescription("Violation because a dependency is out of the accepted version range!")
      .setSeverity(RulePriority.CRITICAL);

  /**
   * Constructor for the RuleRepository, defines its name and it's language key.
   */
  @SuppressWarnings("deprecation")
  public DependencyCheckRuleRepository() {
    super(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY, Java.KEY);
    setName("Dependency Check");
  }

  @Override
  public List<Rule> createRules() {
    return Arrays.asList(UNLISTED, WRONG_VERSION);
  }
}
