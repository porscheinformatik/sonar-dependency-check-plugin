package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sonar.api.design.Dependency;
import org.sonar.api.resources.Library;

public final class Utilities
{
    private static final String EVERY_VERSION_ALLOWED = "[^\\Q([])\\E]*";
    private static final String SPLIT_BY_DOT = "\\.";

    private Utilities()
    {

    }

    public static boolean dependencyInList(Dependency d)
    {
        boolean inList = false;

        List<ProjectDependency> availableDependencies = new ArrayList<ProjectDependency>();

        availableDependencies.add(new ProjectDependency("org.codehaus", ""));
        availableDependencies.add(new ProjectDependency("com.puppycrawl", "5.5"));

        for (ProjectDependency projectDependency : availableDependencies)
        {
            if (d.getTo().getKey().toString().contains(projectDependency.getTitle()))
            {
                return true;
            }
        }

        return inList;
    }

    public static boolean dependencyInList(Dependency d, List<ProjectDependency> availableDependencies)
    {
        for (ProjectDependency projectDependency : availableDependencies)
        {
            if (d.getTo().getKey().toString().contains(projectDependency.getTitle()))
            {
                return true;
            }
        }

        return false;
    }

    public static boolean dependencyInVersionRange(Dependency d, List<ProjectDependency> availableDependencies)
    {
        for (ProjectDependency projectDependency : availableDependencies)
        {
            if (d.getTo().getKey().toString().contains(projectDependency.getTitle()))
            {
                Library l = (Library) d.getTo();

                return versionAllowed(l.getVersion(), projectDependency.getVersionRange());
            }
        }
        return false;
    }

    private static boolean versionAllowed(String versionUsed, String versionRange)
    {

        String[] subVersions = versionUsed.split(SPLIT_BY_DOT);

        String regEx;
        String temp;

        if (versionRange.matches(EVERY_VERSION_ALLOWED))
        {
            return true;
        }

        regEx = "(.*)";

        temp = subVersions[0];
        for (int i = 1; i < subVersions.length; i++)
        {
            temp += SPLIT_BY_DOT + subVersions[i];
        }

        regEx += temp + "\\](.*)|(.*)\\[" + temp + "(.*)";
        if (versionRange.matches(regEx))
        {
            return true;
        }

        if (checkOpenRange(versionRange, subVersions))
        {
            return true;
        }
        if (checkClosedRange(versionRange, subVersions))
        {
            return true;
        }

        return false;
    }

    private static boolean checkOpenRange(String versionRange, String[] subVersions)
    {
        String regEx;

        regEx = "[\\Q([\\E],[\\d\\Q.\\E]+[\\Q)]\\E]";

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(versionRange);

        while (m.find())
        {
            String[] foundVersion = m.group().substring(2, m.group().length() - 1).split(SPLIT_BY_DOT);

            if (foundVersionSmallerThanUpperBorder(foundVersion, subVersions))
            {
                return true;
            }
        }

        regEx = "[\\Q([\\E][\\d\\Q.\\E]+,[\\Q)]\\E]";

        p = Pattern.compile(regEx);
        m = p.matcher(versionRange);

        while (m.find())
        {
            String[] foundVersion = m.group().substring(1, m.group().length() - 2).split(SPLIT_BY_DOT);

            if (foundVersionBiggerThanLowerBorder(foundVersion, subVersions))
            {
                return true;
            }
        }
        return false;

    }

    private static boolean checkClosedRange(String versionRange, String[] subVersions)
    {
        String regEx;

        regEx = "[\\Q([\\E][\\d\\Q.\\E]+,[\\d\\Q.\\E]+[\\Q)]\\E]";

        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(versionRange);

        while (m.find())
        {
            String[] foundVersion = m.group().split(",");
            String[] foundLowerBorder = foundVersion[0].substring(1, foundVersion[0].length()).split(SPLIT_BY_DOT);
            String[] foundUpperBorder = foundVersion[1].substring(0, foundVersion[1].length() - 1).split(SPLIT_BY_DOT);

            for (int i = 0; i < foundVersion.length; i++)
            {
                if (getVersionDifference(foundLowerBorder[i], subVersions[i]) > 0
                    || getVersionDifference(foundUpperBorder[i], subVersions[i]) < 0)
                {
                    break;
                }

                if (getVersionDifference(foundLowerBorder[i], subVersions[i]) == 0
                    && getVersionDifference(foundUpperBorder[i], subVersions[i]) > 0)
                {
                    if (foundVersionBiggerThanLowerBorder(foundLowerBorder, subVersions))
                    {
                        return true;
                    }
                }
                else if (getVersionDifference(foundUpperBorder[i], subVersions[i]) == 0
                    && getVersionDifference(foundLowerBorder[i], subVersions[i]) < 0)
                {
                    if (foundVersionSmallerThanUpperBorder(foundUpperBorder, subVersions))
                    {
                        return true;
                    }
                }
                else if (getVersionDifference(foundUpperBorder[i], foundLowerBorder[i]) >= 2)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean foundVersionBiggerThanLowerBorder(String[] foundVersion, String[] subVersions)
    {
        for (int i = 0; i < foundVersion.length; i++)
        {
            if (getVersionDifference(foundVersion[i], subVersions[i]) < 0)
            {
                return true;
            }
            if (getVersionDifference(foundVersion[i], subVersions[i]) > 0)
            {
                return false;
            }
        }
        return false;
    }

    private static boolean foundVersionSmallerThanUpperBorder(String[] foundVersion, String[] subVersions)
    {

        for (int i = 0; i < foundVersion.length; i++)
        {
            if (getVersionDifference(foundVersion[i], subVersions[i]) > 0)
            {
                return true;
            }
            if (getVersionDifference(foundVersion[i], subVersions[i]) < 0)
            {
                return false;
            }
        }
        return false;
    }

    private static int getVersionDifference(String versionRange, String versionUsed)
    {
        return Integer.parseInt(versionRange) - Integer.parseInt(versionUsed);
    }
}
