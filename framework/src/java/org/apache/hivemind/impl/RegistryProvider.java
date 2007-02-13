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

package org.apache.hivemind.impl;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.definition.RegistryDefinition;

/**
 * A registry provider contributes module definitions to a {@link RegistryDefinition}.
 * It must be implemented to participate in the registry autoloading mechanism
 * triggered by {@link RegistryBuilder#autoDetectModules()} and described in 
 * {@link RegistryProviderAutoDetector}.
 * The implementations must have a no arguments constructor.
 * 
 * @author Achim Huegen
 */
public interface RegistryProvider
{
    /**
     * Called during the registry definition phase, before the registry is constructed.
     * New module definitions can be added to the registry and existing can be altered.
     * 
     * @param registryDefinition  the registry definition 
     * @param errorHandler  an error handler to call when errors occur.
     */
    public void process(RegistryDefinition registryDefinition, ErrorHandler errorHandler);
    
}
