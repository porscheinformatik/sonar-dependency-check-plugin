package org.sonar.plugins.dependencycheck;

import org.junit.Test;
import org.sonar.api.design.Dependency;
import org.sonar.api.resources.Library;
import org.sonar.api.resources.Project;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for Utilities class
 *
 * @author YKM
 *
 */
public class UtilitiesTest {

  /**
   * Tests Function Utilities.dependencyInList(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   */
  @Test
  public void dependencyInListTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "1.2.3", null));

    assertTrue(Utilities.dependencyInList(d, allowedProjectDependencies));
  }

  /**
   * Tests Function Utilities.dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   * for cases where every vesion is allowed
   */
  @Test
  public void dependencyVersionRangeAllVersionsTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();

    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "1.2.3", null));
    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "", null));
    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));
  }

  /**
   * Tests Function Utilities.dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   * for cases where only fixed versions are allowed
   */
  @Test
  public void dependencyVersionRangeFixedVersionTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.2.3]", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.2.30]", null));
    assertFalse(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));
  }

  /**
   * Tests Function Utilities.dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   * for cases where open Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeOpenRangeTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.2.0,]", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[,1.2.2]", null));

    assertFalse(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[,1.2.20]", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));
  }
  /**
   * Tests Function Utilities.dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   * for cases where closed Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeClosedRangeTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.2.0,1.2.8)", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.0.0,1.2.3)", null));

    assertFalse(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.2.2,1.2.20]", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));
  }
  /**
   * Tests Function Utilities.dependencyInVersionRange(Dependency d, List<ProjectDependency> allowedProjectDependencies)
   * for cases where multiple Ranges are allowed
   */
  @Test
  public void dependencyVersionRangeMultipleRangesTest() {
    Dependency d = new Dependency(new Project("test.from.key"), new Library("test.to.key", "1.2.3"));
    List<ProjectDependency> allowedProjectDependencies = new ArrayList<ProjectDependency>();
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.0.0,1.2.2)", null));

    assertFalse(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));

    allowedProjectDependencies.remove(0);
    allowedProjectDependencies.add(new ProjectDependency("test.to.key", "[1.0.0,1.2.2),(1.2.2,1.2.20]", null));

    assertTrue(Utilities.dependencyInVersionRange(d, allowedProjectDependencies));
  }
}
