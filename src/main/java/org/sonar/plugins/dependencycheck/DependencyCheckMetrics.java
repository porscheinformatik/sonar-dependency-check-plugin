package org.sonar.plugins.dependencycheck;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

/**
 * Constants and Metrics for the plugin
 */
public final class DependencyCheckMetrics implements Metrics
{
    public static final String DEPENDENCY_CHECK_KEY = "dependencycheck";
    public static final String DEPENDENCY_CHECK_DEPENDENCY_KEY = "dependencycheck.dependency";
    public static final String DEPENDENCY_CHECK_LICENSE_KEY = "dependencycheck.license";
    public static final String DEPENDENCY_CHECK_UNLISTED_KEY = "dependencycheck.unlisted";
    public static final String DEPENDENCY_CHECK_WRONG_VERSION_KEY = "dependencycheck.wrongversion";

    public static final String LIBRARY_GLOBAL_PROPERTY = "sonar.dependencycheck.lib.global";
    public static final String LIBRARY_PROJECT_PROPERTY = "sonar.dependencycheck.lib.project";
    public static final String LIBRARY_KEY_PROPERTY = "sonar.dependencycheck.lib.key";
    public static final String LIBRARY_VERSION_PROPERTY = "sonar.dependencycheck.lib.version";
    public static final String LIBRARY_LICENSE_PROPERTY = "sonar.dependencycheck.lib.license";

    public static final String LICENSE_PROPERTY = "sonar.dependencycheck.license";
    public static final String LICENSE_ID_PROPERTY = "sonar.dependencycheck.license.id";
    public static final String LICENSE_TITLE_PROPERTY = "sonar.dependencycheck.license.title";
    public static final String LICENSE_DESCRIPTION_PROPERTY = "sonar.dependencycheck.license.description";
    public static final String LICENSE_URL_PROPERTY = "sonar.dependencycheck.license.url";
    public static final String LICENSE_SOURCETYPE_PROPERTY = "sonar.dependencycheck.license.sourcetype";
    public static final String LICENSE_COMMERCIAL_PROPERTY = "sonar.dependencycheck.license.commercial";

    public static final Metric DEPENDENCY = new Metric.Builder(DEPENDENCY_CHECK_DEPENDENCY_KEY,
        "Dependency Check - Dependencies",
        Metric.ValueType.DATA)
        .setDescription("Used Dependencies")
        .setDirection(Metric.DIRECTION_WORST)
        .setQualitative(true)
        .setDomain(CoreMetrics.DOMAIN_GENERAL)
        .create();

    public static final Metric LICENSE = new Metric.Builder(DEPENDENCY_CHECK_LICENSE_KEY,
        "Dependency Check - Licenses",
        Metric.ValueType.DATA)
        .setDescription("Used Libraries")
        .setDirection(Metric.DIRECTION_WORST)
        .setQualitative(true)
        .setDomain(CoreMetrics.DOMAIN_GENERAL)
        .create();

    /**
     * {@inheritDoc}
     */
    public List<Metric> getMetrics()
    {
        return Arrays.asList(DEPENDENCY, LICENSE);
    }

}
