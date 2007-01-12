// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.internal;

import org.apache.hivemind.definition.ConfigurationPointDefinition;


/**
 * An extension point that provides configuration data in the form of a list of elements.
 * 
 * @author Howard Lewis Ship
 */
public interface ConfigurationPoint extends ExtensionPoint
{
    /**
     * Returns the container that holds the configuration data. May
     * return a proxy to the actual data (which is constructed only as needed), but user code
     * shouldn't care about that.
     */
    public Object getConfiguration();
    
    /**
     * @return  the type of the container that holds the configuration data
     */
    public Class getConfigurationType();

    public ConfigurationPointDefinition getConfigurationPointDefinition();
}