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

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.SonarException;

/**
 * Wrapper for XML serializing dependencies.
 */
@XmlRootElement(name = "allowed-dependencies")
public class AllowedDependencies {

  private static JAXBContext jaxbContext;
  static {
    try {
      jaxbContext = JAXBContext.newInstance(AllowedDependencies.class);
    }
    catch (JAXBException e) {
      throw new SonarException("Failure creating JAXBContext for AllowedDependencies", e);
    }
  }

  @XmlElement(name = "dependency")
  private List<AllowedDependency> dependencies;

  public List<AllowedDependency> getDependencies() {
    return dependencies;
  }

  /**
   * Load list of allowed dependecies from XML.
   * @param xml XML string
   * @return list of dependencies
   */
  public static List<AllowedDependency> loadFromXml(String xml) {

    if(StringUtils.isEmpty(xml)) {
      return Collections.emptyList();
    }

    try {
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      AllowedDependencies allowedDependencies = (AllowedDependencies) unmarshaller.unmarshal(new StringReader(xml));
      return allowedDependencies.dependencies;
    }
    catch (JAXBException e) {
      throw new SonarException("Failure parsing XML for allowed dependencies", e);
    }
  }
}
