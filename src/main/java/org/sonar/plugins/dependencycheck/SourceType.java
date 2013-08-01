package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;

/**
 * This enum represents the type of source code which is allowed by a license.
 */
public enum SourceType
{
    CLOSED,
    OPENSOURCE_COPYLEFT,
    OPENSOURCE_NO_COPYLEFT;

    /**
     * creates a string list of all the possibel source type names
     * 
     * @return the created list
     */
    public static List<String> getSourceTypes()
    {
        List<String> types = new ArrayList<String>();

        for (SourceType type : SourceType.values())
        {
            types.add(type.name());
        }
        return types;
    }
}
