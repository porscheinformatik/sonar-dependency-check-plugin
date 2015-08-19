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

import java.util.List;
import java.util.SortedSet;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.design.Dependency;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.resources.ResourceUtils;

/**
 * This class has different functions needed in various other classes.
 */
public final class Utilities {
  private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);
  private static final License NO_LICENSE = new License("", "No License found", "", "", SourceType.CLOSED, false);

  private Utilities() {
  }

  /**
   * Search for a license by its ID (the one that is written on top of the properties file with the tag:
   * 'licenses.list').
   *
   * @param licenseName - name of the used license
   * @param allowedLicenses - list of allowed licenses
   * @return the found license
   */
  static License getLicenseByNameOrId(String licenseName, List<License> allowedLicenses) {
    if (licenseName != null) {
      for (License license : allowedLicenses) {
        if (license.getTitle().contains(licenseName) || license.getId().equals(licenseName)) {
          return license;
        }
      }
    }

    return NO_LICENSE;
  }

  /**
   * Checks if the version used is in the allowed range.
   *
   * @param versionUsed - used versions
   * @param versionRange - allowed versions
   * @return true if version used is in range
   */
  static boolean versionAllowed(String versionUsed, String versionRange) {
    try {
      versionRange = StringUtils.isEmpty(versionRange) ? "0" : versionRange;
      VersionRange versionSpec = VersionRange.createFromVersionSpec(versionRange);
      return versionSpec.containsVersion(new DefaultArtifactVersion(versionUsed));
    }
    catch (InvalidVersionSpecificationException e) {
      LOG.warn("Error parsing version range: " + versionRange, e);
      return false;
    }
  }

  /**
   * Searches the name (title) of the license of the dependency.
   *
   * @param dependencyKey key of the used dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return name of the license or a empty String if nothing has been found
   */
  static String getLicenseName(String dependencyKey, List<AllowedDependency> allowedProjectDependencies) {
    AllowedDependency pd = searchForProjectDependency(dependencyKey, allowedProjectDependencies);
    return pd != null && pd.getLicense() != null ? pd.getLicense().getTitle() : "";
  }

  /**
   * Searches the license of the used dependency.
   *
   * @param dependencyKey key of the used dependency
   * @param allowedProjectDependencies - allowed dependencies
   * @return the found license or null if nothing has been found
   */
  static License getLicense(String dependencyKey, List<AllowedDependency> allowedProjectDependencies) {
    AllowedDependency pd = searchForProjectDependency(dependencyKey, allowedProjectDependencies);

    return pd != null ? pd.getLicense() : null;

  }

  /**
   * Searches for a dependency in a list of allowed dependencies.
   *
   * @param dependency currently handled dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return version range of the found dependency or an empty string
   */
  static String getDependencyVersionRange(Resource dependency, List<AllowedDependency> allowedProjectDependencies) {
    AllowedDependency pd = searchForProjectDependency(dependency.getKey(), allowedProjectDependencies);
    return pd != null ? pd.getVersionRange() : "";
  }

  /**
   * Searches for a project dependency in the list of the allowed dependency.
   *
   * @param dependencyKey the key of the currently handled dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return found project dependency
   */
  static AllowedDependency searchForProjectDependency(String dependencyKey, List<AllowedDependency> allowedProjectDependencies) {
    for (AllowedDependency projectDependency : allowedProjectDependencies) {
      if (dependencyKey.startsWith(projectDependency.getKey())) {
        return projectDependency;
      }
    }

    return null;
  }

  /**
   * Concatenates a SortedSet of Strings with a semicolon between them.
   *
   * @param sortedDependencies - SortedSet of Strings
   * @return concatenated string
   */
  public static String concatStringList(SortedSet<String> sortedDependencies) {
    String concatenatedString = "";
    for (String s : sortedDependencies) {
      concatenatedString = concatenatedString.concat(s + ";");
    }
    return concatenatedString;
  }

  /**
   * Checks if the dependency has the same root as the project.
   *
   * @param d - dependency
   * @return true if it has the same root, false if not
   */
  public static boolean hasSameRoot(Dependency d) {
    Project p = null;
    if (ResourceUtils.isProject(d.getTo()) && !ResourceUtils.isLibrary(d.getTo())) {
      p = (Project) d.getTo();
      return p.getRoot().equals(((Project) d.getFrom()).getRoot());
    }
    return false;
  }

  /**
   * checks if the maven scope of the dependency is in the list of allowed scopes
   *
   * @param d - checked Dependency
   * @param allowedScopes - allowed maven scopes
   * @return true if maven scope of d is in allowedScopes
   */
  public static boolean inCheckScope(Dependency d, List<String> allowedScopes) {
    for (String scope : allowedScopes) {
      if (scope.equals(d.getUsage())) {
        return true;
      }
    }
    return false;
  }

}
