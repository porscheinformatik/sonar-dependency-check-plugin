package org.sonar.plugins.dependencycheck;

import java.util.ArrayList;
import java.util.List;

public enum SourceType
{
    CLOSED, OPENSOURCE_COPYLEFT, OPENSOURCE_NO_COPYLEFT;

    public static List<String> getSourceTypes()
    {
        List<String> types= new ArrayList<String>();
        
        for (SourceType type : SourceType.values())
        {
            types.add(type.name());
        }
        return types;
    }
}
