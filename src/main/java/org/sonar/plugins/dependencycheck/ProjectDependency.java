package org.sonar.plugins.dependencycheck;

/**
 * This class stores a Dependency which is used by the plugin
 * @author YKM
 *
 */
public class ProjectDependency
{
    private String key;
    private String versionRange;
    private License license;

    /**
     * standard constructor for a ProjectDependency
     * initializes everything as empty string or null
     */
    public ProjectDependency()
    {
        this.key = "";
        this.versionRange = "";
        this.license = null;
    }
    /**
     * constructor for a ProjectDependency
     * @param title - title of the dependency
     * @param versionRange - version range of the dependency
     * @param license - license of the dependency
     */
    public ProjectDependency(String title, String versionRange, License license)
    {
        this.key = title;
        this.versionRange = versionRange;
        this.license = license;
    }

    public String getVersionRange()
    {
        return versionRange;
    }

    public void setVersionRange(String versionRange)
    {
        this.versionRange = versionRange != null ? versionRange : "";
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public License getLicense()
    {
        return license;
    }

    public void setLicense(License license)
    {
        this.license = license;
    }

}
