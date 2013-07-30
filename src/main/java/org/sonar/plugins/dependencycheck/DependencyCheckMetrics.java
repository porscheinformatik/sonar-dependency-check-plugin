package org.sonar.plugins.dependencycheck;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

public final class DependencyCheckMetrics implements Metrics
{
    public static final String DEPENDENCY_CHECK_KEY = "dependencycheck";
    public static final String DEPENDENCY_CHECK_TESTRULE_KEY = "dependencycheck.testrule";
    public static final String DEPENDENCY_CHECK_UNLISTED_KEY = "dependencycheck.unlisted";
    public static final String DEPENDENCY_CHECK_WRONG_VERSION_KEY = "dependencycheck.wrongversion";

    public static final String LIBRARY_PROPERTY = "sonar.dependencycheck.lib";
    public static final String LIBRARY_KEY_PROPERTY = "sonar.dependencycheck.lib.key";
    public static final String LIBRARY_VERSION_PROPERTY = "sonar.dependencycheck.lib.version";
    public static final String LIBRARY_LICENSE_PROPERTY = "sonar.dependencycheck.lib.license";
    
    public static final String LICENSE_PROPERTY = "sonar.dependencycheck.license";
    public static final String LICENSE_TITLE_PROPERTY = "sonar.dependencycheck.license.title";
    public static final String LICENSE_DESCRIPTION_PROPERTY = "sonar.dependencycheck.license.description";
    public static final String LICENSE_URL_PROPERTY = "sonar.dependencycheck.license.url";
    public static final String LICENSE_SOURCETYPE_PROPERTY = "sonar.dependencycheck.license.sourcetype";
    public static final String LICENSE_COMMERCIAL_PROPERTY = "sonar.dependencycheck.license.commercial";

    public static final Metric DEPENDENCY = new Metric.Builder(DEPENDENCY_CHECK_KEY, "Dependency Check",
        Metric.ValueType.DATA)
        .setDescription("Used Dependency")
        .setDirection(Metric.DIRECTION_WORST)
        .setQualitative(true)
        .setDomain(CoreMetrics.DOMAIN_GENERAL)
        .create();
    
    
    public List<Metric> getMetrics()
    {
        return Arrays.asList(DEPENDENCY);
    }

}
