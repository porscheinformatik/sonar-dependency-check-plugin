package org.sonar.plugins.dependencycheck;

public class ProjectDependency
{
    private String key;
    private String versionRange;
    private String licenseName;
    private License license;

    public ProjectDependency()
    {
        this.key = "";
        this.versionRange = "";
        this.licenseName = "";
        this.license = null;
    }

    public ProjectDependency(String title, String versionRange, String licenseName)
    {
        this.key = title;
        this.versionRange = versionRange;
        this.licenseName = licenseName;
        // TODO set license name
    }

    public String getVersionRange()
    {
        return versionRange;
    }

    public void setVersionRange(String versionRange)
    {
        this.versionRange = versionRange != null ? versionRange :  "";
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getLicenseName()
    {
        return licenseName;
    }

    public void setLicenseName(String licenseName)
    {
        this.licenseName = licenseName;
        //TODO set License
    }

    public License getLicense()
    {
        return license;
    }    

}
