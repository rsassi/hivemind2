package org.apache.hivemind.definition;

import org.apache.hivemind.internal.ConfigurationPoint;

/**
 * Context for execution of a {@link Contribution}.
 * 
 * Allows to manipulate a configuration from a {@link Contribution}.
 * The new contribution can be merged with the existing configuration data or
 * the data may be changed or replaced. 
 * 
 * @author Huegen
 */
public interface ContributionContext extends ConstructionContext
{
    /**
     * @return  the configuration point that is currently constructed
     */
    public ConfigurationPoint getConfigurationPoint();
    
    /**
     * @return  the configuration data already provided by other contributions that were processed before.
     *          Null, if no data has be contributed before.
     */
    public Object getConfigurationData();
    
    /**
     * Replaces all configuration data with <code>data</code>. Overrides data provided by other contributions
     * so should only be called when getConfigurationData() returns null.
     *  
     * @param data
     */
    public void setConfigurationData(Object data);
    
    /**
     * Merges contribution data with the data already provided by other contributions.
     * Automatic merging works for standard collections only. 
     * Handles the case that no data has been provided before (getConfigurationData() returns null)
     * 
     * @param contributionData  the data to merge. Must be compatible with the type of the configuration.
     */
    public void mergeContribution(Object contributionData);
}
