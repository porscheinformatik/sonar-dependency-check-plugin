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

import javax.xml.bind.annotation.XmlElement;

/**
 * This class stores a Dependency which is used by the plugin.
 */
public class AllowedDependency {
  @XmlElement
  private String key;
  @XmlElement
  private String versionRange;
  @XmlElement
  private String licenseId;
  private License license;

  /**
   * Standard constructor for a {@link AllowedDependency} - initializes everything as empty string or null.
   */
  public AllowedDependency() {
    this.key = "";
    this.versionRange = "";
    this.license = null;
  }

  /**
   * Constructor for a {@link AllowedDependency}
   * 
   * @param title - title of the dependency
   * @param versionRange - version range of the dependency
   * @param license - license of the dependency
   */
  public AllowedDependency(String title, String versionRange, License license) {
    this.key = title;
    this.versionRange = versionRange;
    this.license = license;
  }

  public String getVersionRange() {
    return versionRange;
  }

  public String getKey() {
    return key;
  }

  public String getLicenseId() {
    return licenseId;
  }

  public License getLicense() {
    return license;
  }

  public void setLicense(License license) {
    this.license = license;
  }
}
