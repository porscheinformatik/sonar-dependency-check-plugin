package org.sonar.plugins.dependencycheck;

public class ProjectDependency
{
    private String key;
    private String versionRange;
    

    public ProjectDependency(String title, String versionRange)
    {
        this.key = title;
        this.versionRange = versionRange;
    }

    public String getTitle()
    {
        return key;
    }

    public void setTitle(String title)
    {
        this.key = title;
    }

    public String getVersionRange()
    {
        return versionRange;
    }

    public void setVersionRange(String versionRange)
    {
        this.versionRange = versionRange;
    }

    
}
