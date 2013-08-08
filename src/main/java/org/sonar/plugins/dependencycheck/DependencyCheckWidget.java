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
