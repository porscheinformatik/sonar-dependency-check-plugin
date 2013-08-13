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

import static java.lang.Boolean.TRUE;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

/**
 * Constants and Metrics for the plugin.
 */
public final class DependencyCheckMetrics implements Metrics {

  public static final String DEPENDENCY_CHECK_KEY = "dependencycheck";
  public static final String DEPENDENCY_CHECK_DEPENDENCY_KEY = "dependencycheck.dependency";
  public static final String DEPENDENCY_CHECK_LICENSE_KEY = "dependencycheck.license";
  public static final String DEPENDENCY_CHECK_UNLISTED_KEY = "dependencycheck.unlisted";
  public static final String DEPENDENCY_CHECK_WRONG_VERSION_KEY = "dependencycheck.wrongversion";

  public static final String LIBRARY_GLOBAL_PROPERTY = "sonar.dependencycheck.lib.global";
  public static final String LIBRARY_PROJECT_PROPERTY = "sonar.dependencycheck.lib.project";
  public static final String LIBRARY_KEY_PROPERTY = "sonar.dependencycheck.lib.key";
  public static final String LIBRARY_VERSION_PROPERTY = "sonar.dependencycheck.lib.version";
  public static final String LIBRARY_LICENSE_PROPERTY = "sonar.dependencycheck.lib.license";

  public static final String LICENSE_PROPERTY = "sonar.dependencycheck.license";
  public static final String LICENSE_ID_PROPERTY = "sonar.dependencycheck.license.id";
  public static final String LICENSE_TITLE_PROPERTY = "sonar.dependencycheck.license.title";
  public static final String LICENSE_DESCRIPTION_PROPERTY = "sonar.dependencycheck.license.description";
  public static final String LICENSE_URL_PROPERTY = "sonar.dependencycheck.license.url";
  public static final String LICENSE_SOURCETYPE_PROPERTY = "sonar.dependencycheck.license.sourcetype";
  public static final String LICENSE_COMMERCIAL_PROPERTY = "sonar.dependencycheck.license.commercial";

  public static final String SCOPE_TEST_PROPERTY = "dependencycheck.scope.test";
  public static final String SCOPE_PROVIDED_PROPERTY = "dependencycheck.scope.provided";
  public static final String SCOPE_COMPILE_PROPERTY = "dependencycheck.scope.compile";
  public static final String SCOPE_RUNTIME_PROPERTY = "dependencycheck.scope.runtime";

  public static final Metric DEPENDENCY = new Metric.Builder(DEPENDENCY_CHECK_DEPENDENCY_KEY,
      "Dependency Check - Dependencies",
      Metric.ValueType.DATA)
      .setDescription("Used Dependencies")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(TRUE)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  public static final Metric LICENSE = new Metric.Builder(DEPENDENCY_CHECK_LICENSE_KEY,
      "Dependency Check - Licenses",
      Metric.ValueType.DATA)
      .setDescription("Used Libraries")
      .setDirection(Metric.DIRECTION_WORST)
      .setQualitative(TRUE)
      .setDomain(CoreMetrics.DOMAIN_GENERAL)
      .create();

  /**
   * {@inheritDoc}
   */
  public List<Metric> getMetrics() {
    return Arrays.asList(DEPENDENCY, LICENSE);
  }

}
