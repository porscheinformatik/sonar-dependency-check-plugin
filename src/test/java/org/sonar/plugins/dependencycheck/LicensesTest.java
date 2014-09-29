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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class LicensesTest {
  @Test
  public void loadFromXml() throws Exception {
    StringWriter buffer = new StringWriter();
    IOUtils.copy(new InputStreamReader(LicensesTest.class.getResourceAsStream("/licenses.xml"), "UTF-8"), buffer);
    List<License> licenses = Licenses.loadFromXml(buffer.toString());

    assertThat(licenses.size(), is(3));
    assertThat(licenses.get(0).getId(), is("Apache-2.0"));
    assertThat(licenses.get(1).getSourceType(), is(SourceType.OPENSOURCE_NO_COPYLEFT));
  }

}
