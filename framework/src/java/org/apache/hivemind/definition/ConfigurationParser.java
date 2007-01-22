package org.apache.hivemind.definition;

import java.io.InputStream;


public interface ConfigurationParser
{
    /**
     * Parses a configuration and returns the converted data.
     * @param context  context
     * @param data     the data to parse as stream. 
     * @return  the converted data.
     */
    public Object parse(ContributionContext context, InputStream data);
    
    /**
     * Parses a configuration and returns the converted data.
     * @param context  context
     * @param data     the data to parse. What kind of object is expected here is parser specific. 
     * @return  the converted data.
     */
    public Object parse(ContributionContext context, Object data);
}
