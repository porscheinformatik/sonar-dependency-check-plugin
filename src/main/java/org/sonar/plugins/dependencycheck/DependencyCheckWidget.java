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

import org.sonar.api.web.AbstractRubyTemplate;
import org.sonar.api.web.Description;
import org.sonar.api.web.RubyRailsWidget;
import org.sonar.api.web.UserRole;
import org.sonar.api.web.WidgetCategory;

/**
 * Widget to show the measured data
 */
@UserRole(UserRole.USER)
@Description("Widget for the plugin Dependency check. Shows the used Dependencies and their status (ok, wrong version, unlisted)")
@WidgetCategory({"Dependencies"})
public final class DependencyCheckWidget extends AbstractRubyTemplate implements RubyRailsWidget {
  public String getId() {
    return "dependencycheck_widget";
  }

  public String getTitle() {
    return "Dependency Check Widget";
  }

  @Override
  protected String getTemplatePath() {
    return "/org/sonar/plugins/dependencycheck/DependencyCheckWidget.html.erb";
  }
}
