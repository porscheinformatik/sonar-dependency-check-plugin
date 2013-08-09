package org.sonar.plugins.dependencycheck;

import java.util.List;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.design.Dependency;
import org.sonar.api.resources.Library;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.ResourceUtils;

/**
 * This class has different functions needed in various other classes.
 */
public final class Utilities {
  private static final String EVERY_VERSION_ALLOWED = "[^\\Q([])\\E]*";
  private static final String SPLIT_BY_DOT = "\\.";
  private static final License NO_LICENSE = new License();
  static {
    NO_LICENSE.setTitle("No License found");
    NO_LICENSE.setCommercial(false);
    NO_LICENSE.setUrl("");
    NO_LICENSE.setSourceType(SourceType.CLOSED);
  }

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
   * Searches through the list of allowed dependencies.
   *
   * @param d - currently handled dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return true if the dependency is in the allowed list
   */
  static boolean dependencyInList(Dependency d, List<ProjectDependency> allowedProjectDependencies) {
    ProjectDependency pd = searchForProjectDependency(d, allowedProjectDependencies);

    return pd != null ? true : false;
  }

  /**
   * searches for the currently handled dependency and checks if it is in the allowed version range (true)
   *
   * @param d - currently handled dependency
   * @param allowedProjectDependencies - list of available dependencies
   * @return dependency in version range
   */
  static boolean dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies) {
    ProjectDependency pd = searchForProjectDependency(d, allowedProjectDependencies);
    return pd != null ? versionAllowed(((Library) d.getTo()).getVersion(), pd.getVersionRange()) : false;
  }

  /**
   * Checks if the version used is in the allowed range.
   *
   * @param versionUsed - used versions
   * @param versionRange - allowed versions
   * @return true if version used is in range
   */
  private static boolean versionAllowed(String versionUsed, String versionRange) {

    String[] subVersions = versionUsed.split(SPLIT_BY_DOT);

    String regEx;
    String temp;

    if (versionRange.matches(EVERY_VERSION_ALLOWED)) {
      return true;
    }

    regEx = "(.*)[\\Q[,\\E]";

    temp = subVersions[0];
    for (int i = 1; i < subVersions.length; i++) {
      temp = temp.concat(SPLIT_BY_DOT + subVersions[i]);
    }

    regEx += temp + "\\](.*)|(.*)\\[" + temp + "[\\Q],\\E](.*)";
    if (versionRange.matches(regEx)) {
      return true;
    }

    if (checkOpenRange(versionRange, subVersions)) {
      return true;
    }
    if (checkClosedRange(versionRange, subVersions)) {
      return true;
    }

    return false;
  }

  /**
   * Checks whether versionRange is an open range and if so it checks if subVersions is allowed in it.
   *
   * @param versionRange - allowed version range
   * @param subVersions - version used split by dots (into sub versions)
   * @return true if version is allowed
   */
  private static boolean checkOpenRange(String versionRange, String[] subVersions) {
    String regEx;

    regEx = "[\\Q([\\E],[\\d\\Q.\\E]+[\\Q)]\\E]";

    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(versionRange);

    while (m.find()) {
      String[] foundVersion = m.group().substring(2, m.group().length() - 1).split(SPLIT_BY_DOT);

      if (foundVersionSmallerThanUpperBorder(foundVersion, subVersions)) {
        return true;
      }
    }

    regEx = "[\\Q([\\E][\\d\\Q.\\E]+,[\\Q)]\\E]";

    p = Pattern.compile(regEx);
    m = p.matcher(versionRange);

    while (m.find()) {
      String[] foundVersion = m.group().substring(1, m.group().length() - 2).split(SPLIT_BY_DOT);

      if (foundVersionBiggerThanLowerBorder(foundVersion, subVersions)) {
        return true;
      }
    }
    return false;

  }

  /**
   * Checks whether versionRange is a closed range and if so it checks if subVersions is allowed in it.
   *
   * @param versionRange - allowed version range
   * @param subVersions - version used split by dots (into sub versions)
   * @return true if version is allowed
   */
  private static boolean checkClosedRange(String versionRange, String[] subVersions) {
    String regEx;

    regEx = "[\\Q([\\E][\\d\\Q.\\E]+,[\\d\\Q.\\E]+[\\Q)]\\E]";

    Pattern p = Pattern.compile(regEx);
    Matcher m = p.matcher(versionRange);

    while (m.find()) {
      String[] foundVersion = m.group().split(",");
      String[] foundLowerBorder = foundVersion[0].substring(1, foundVersion[0].length()).split(SPLIT_BY_DOT);
      String[] foundUpperBorder = foundVersion[1].substring(0, foundVersion[1].length() - 1).split(SPLIT_BY_DOT);

      int maxCount = Math.max(foundLowerBorder.length, foundUpperBorder.length);

      for (int i = 0; i < maxCount; i++) {
        if (getVersionDifference(foundLowerBorder[i], subVersions[i]) > 0
          || getVersionDifference(foundUpperBorder[i], subVersions[i]) < 0) {
          break;
        }

        if (isInRange(foundLowerBorder, foundUpperBorder, subVersions, i)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if the version is allowed between the 2 borders of the closed range.
   *
   * @param foundLowerBorder - lower border of the closed range, split by dots
   * @param foundUpperBorder - upper border of the closed range, split by dots
   * @param subVersions - version used split by dots
   * @param i - the currently handled subversion
   * @return true if version is allowed
   */
  private static boolean isInRange(String[] foundLowerBorder, String[] foundUpperBorder, String[] subVersions, int i) {
    if (getVersionDifference(foundLowerBorder[i], subVersions[i]) == 0
      && getVersionDifference(foundUpperBorder[i], subVersions[i]) > 0) {
      if (foundVersionBiggerThanLowerBorder(foundLowerBorder, subVersions)) {
        return true;
      }
    }
    else if (getVersionDifference(foundUpperBorder[i], subVersions[i]) == 0
      && getVersionDifference(foundLowerBorder[i], subVersions[i]) < 0) {
      if (foundVersionSmallerThanUpperBorder(foundUpperBorder, subVersions)) {
        return true;
      }
    }
    else if (getVersionDifference(foundUpperBorder[i], foundLowerBorder[i]) >= 2) {
      return true;
    }

    return false;
  }

  /**
   * checks if used version is bigger than the lower border
   *
   * @param foundVersion - lower border of version range
   * @param subVersions - used version
   * @return true if version is allowed
   */
  private static boolean foundVersionBiggerThanLowerBorder(String[] foundVersion, String[] subVersions) {
    for (int i = 0; i < foundVersion.length; i++) {
      if (getVersionDifference(foundVersion[i], subVersions[i]) < 0) {
        return true;
      }
      if (getVersionDifference(foundVersion[i], subVersions[i]) > 0) {
        return false;
      }
    }
    return false;
  }

  /**
   * Checks if used version is bigger than the lower border.
   *
   * @param foundVersion - upper border of version range
   * @param subVersions - used version
   * @return true if version is allowed
   */
  private static boolean foundVersionSmallerThanUpperBorder(String[] foundVersion, String[] subVersions) {

    for (int i = 0; i < foundVersion.length; i++) {
      if (getVersionDifference(foundVersion[i], subVersions[i]) > 0) {
        return true;
      }
      if (getVersionDifference(foundVersion[i], subVersions[i]) < 0) {
        return false;
      }
    }
    return false;
  }

  /**
   * Calculates the difference between two versions.
   *
   * @param versionRange - allowed version range
   * @param versionUsed - used version range
   * @return the difference between the version
   */
  private static int getVersionDifference(String versionRange, String versionUsed) {
    return Integer.parseInt(versionRange) - Integer.parseInt(versionUsed);
  }

  /**
   * Searches the name (title) of the license of the dependency.
   *
   * @param d - used dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return name of the license or a empty String if nothing has been found
   */
  static String getLicenseName(Dependency d, List<ProjectDependency> allowedProjectDependencies) {
    ProjectDependency pd = searchForProjectDependency(d, allowedProjectDependencies);
    return pd != null ? pd.getLicense().getTitle() : "";
  }

  /**
   * Searches the license of the used dependency.
   *
   * @param d - used dependency
   * @param allowedProjectDependencies - allowed dependencies
   * @return the found license or null if nothing has been found
   */
  static License getLicense(Dependency d, List<ProjectDependency> allowedProjectDependencies) {
    ProjectDependency pd = searchForProjectDependency(d, allowedProjectDependencies);

    return pd != null ? pd.getLicense() : null;

  }

  /**
   * Searches for a dependency in a list of allowed dependencies.
   *
   * @param d - currently handled dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return version range of the found dependency or an empty string
   */
  static String getDependencyVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies) {
    ProjectDependency pd = searchForProjectDependency(d, allowedProjectDependencies);
    return pd != null ? pd.getVersionRange() : "";
  }

  /**
   * Searches for a project dependency in the list of the allowed dependency.
   *
   * @param d - currently handled dependency
   * @param allowedProjectDependencies - list of allowed dependencies
   * @return found project dependency
   */
  private static ProjectDependency searchForProjectDependency(Dependency d,
      List<ProjectDependency> allowedProjectDependencies) {
    for (ProjectDependency projectDependency : allowedProjectDependencies) {
      if (d.getTo().getKey().toString().contains(projectDependency.getKey())) {
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
