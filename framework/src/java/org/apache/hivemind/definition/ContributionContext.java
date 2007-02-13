// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
     * @param data  the data
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
