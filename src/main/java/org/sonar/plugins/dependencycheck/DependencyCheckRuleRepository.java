package org.sonar.plugins.dependencycheck;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.resources.Java;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.rules.RuleRepository;
/**
 * Repository for the rules used in the plugin
 */
@SuppressWarnings("deprecation")
public final class DependencyCheckRuleRepository extends RuleRepository
{
    public static final Rule UNLISTED = Rule.create(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
        DependencyCheckMetrics.DEPENDENCY_CHECK_UNLISTED_KEY,
        "Unlisted Dependency Violation [dependency-check]")
        .setDescription("Violation because a dependency is not listed!")
        .setSeverity(RulePriority.BLOCKER);

    public static final Rule WRONG_VERSION = Rule.create(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY,
        DependencyCheckMetrics.DEPENDENCY_CHECK_WRONG_VERSION_KEY,
        "Dependency with wrong Version Violation [dependency-check]")
        .setDescription("Violation because a dependency is out of the accepted version range!")
        .setSeverity(RulePriority.CRITICAL);

    /**
     * constructor for the RuleRepository, defines its name and it's language key
     */
    public DependencyCheckRuleRepository()
    {
        super(DependencyCheckMetrics.DEPENDENCY_CHECK_KEY, Java.KEY);
        setName("Dependency Check");
    }

    @Override
    public List<Rule> createRules()
    {
        return Arrays.asList(UNLISTED, WRONG_VERSION);
    }
}
