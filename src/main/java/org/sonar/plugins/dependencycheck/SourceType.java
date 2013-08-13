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

/**
 * This enum represents the type of source code which is allowed by a license.
 */
public enum SourceType {
  CLOSED,
  OPENSOURCE_COPYLEFT,
  OPENSOURCE_NO_COPYLEFT;

  /**
   * Creates a string list of all the possible source type names.
   * 
   * @return the created list
   */
  public static List<String> getSourceTypes() {
    List<String> types = new ArrayList<String>();

    for (SourceType type : SourceType.values()) {
      types.add(type.name());
    }
    return types;
  }
}
