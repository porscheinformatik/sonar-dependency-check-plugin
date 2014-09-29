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
 * This class stores a License which is used in the plugin.
 */
public class License {
  @XmlElement
  private String id;
  @XmlElement
  private String title;
  @XmlElement
  private String description;
  @XmlElement
  private String url;
  @XmlElement
  private SourceType sourceType;
  @XmlElement
  private boolean commercial;

  /**
   * Creates empty license.
   */
  public License() {
    super();
  }

  /**
   * @param id .
   * @param title .
   * @param description .
   * @param url .
   * @param sourceType .
   * @param commercial .
   */
  public License(String id, String title, String description, String url, SourceType sourceType, boolean commercial) {
    super();
    this.id = id;
    this.title = title;
    this.description = description;
    this.url = url;
    this.sourceType = sourceType;
    this.commercial = commercial;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getUrl() {
    return url == null ? "" : url;
  }

  public SourceType getSourceType() {
    return sourceType;
  }

  public boolean isCommercial() {
    return commercial;
  }
}
