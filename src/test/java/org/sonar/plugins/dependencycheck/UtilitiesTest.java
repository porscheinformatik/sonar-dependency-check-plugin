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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit tests for Utilities class
 */
public class UtilitiesTest {

  /**
   * Tests Function Utilities.dependencyInList(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   */
  @Test
  public void dependencyInListTest() {
    List<AllowedDependency> allowedProjectDependencies = new ArrayList<AllowedDependency>();
    allowedProjectDependencies.add(new AllowedDependency("test.to.key", "1.2.3", null));

    assertNotNull(Utilities.searchForProjectDependency("test.to.key", allowedProjectDependencies));
  }

  /**
   * Tests Function {@link Utilities#versionAllowed(String, String)} for cases where every vesion is allowed
   */
  @Test
  public void dependencyVersionRangeAllVersionsTest() {
    assertTrue(Utilities.versionAllowed("1.2.3", "1.2.3"));
    assertTrue(Utilities.versionAllowed("1.2.3", "0"));
    assertTrue(Utilities.versionAllowed("1.2.3", ""));
  }

  /**
   * Tests Function {@link Utilities#versionAllowed(String, String)} for cases where only fixed versions are allowed
   */
  @Test
  public void dependencyVersionRangeFixedVersionTest() {
    assertTrue(Utilities.versionAllowed("1.2.3", "[1.2.3]"));
    assertFalse(Utilities.versionAllowed("1.2.3", "[1.2.30]"));
  }

  /**
   * Tests Function {@link Utilities#versionAllowed(String, String)} for cases where open Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeOpenRangeTest() {
    assertTrue(Utilities.versionAllowed("1.2.3", "[1.2.0,]"));
    assertFalse(Utilities.versionAllowed("1.2.3", "[,1.2.2]"));
    assertTrue(Utilities.versionAllowed("1.2.3", "[,1.2.20]"));
  }

  /**
   * Tests Function {@link Utilities#versionAllowed(String, String)} for cases where closed Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeClosedRangeTest() {
    assertTrue(Utilities.versionAllowed("1.2.3", "[1.2.0,1.2.8)"));
    assertFalse(Utilities.versionAllowed("1.2.3", "[1.0.0,1.2.3)"));
    assertTrue(Utilities.versionAllowed("1.2.3", "[1.2.2,1.2.20]"));
  }

  /**
   * Tests Function {@link Utilities#versionAllowed(String, String)} for cases where multiple Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeMultipleRangesTest() {
    assertFalse(Utilities.versionAllowed("1.2.3", "[1.0.0,1.2.2)"));
    assertTrue(Utilities.versionAllowed("1.2.3", "[1.0.0,1.2.2),(1.2.2,1.2.20]"));
    assertTrue(Utilities.versionAllowed("4.1.5.RELEASE", "[4.1.4.RELEASE,),[4.0.9.RELEASE]"));
    assertTrue(Utilities.versionAllowed("4.0.9.RELEASE", "[4.1.4.RELEASE,),[4.0.9.RELEASE]"));
    assertTrue(Utilities.versionAllowed("3.1.9.RELEASE", "[3.1.4.RELEASE,3.2),[3.2.8.RELEASE,3.3),[4.0.7.RELEASE,)"));
    assertTrue(Utilities.versionAllowed("4.0.9.RELEASE", "[3.1.4.RELEASE,3.2),[3.2.8.RELEASE,3.3),[4.0.7.RELEASE,)"));
    assertFalse(Utilities.versionAllowed("3.2.7.RELEASE", "[4.0.7.RELEASE,),[3.1.4.RELEASE,3.2),[3.2.8.RELEASE,3.3)"));
  }
}
