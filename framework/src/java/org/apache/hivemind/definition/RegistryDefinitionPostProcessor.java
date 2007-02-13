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

import org.apache.hivemind.ErrorHandler;

public interface RegistryDefinitionPostProcessor
{
    /**
     * Is called after all registry providers have run through their process method.
     * That means, all extension points of all modules are now known to the registry
     * definition.
     * 
     * @param registryDefinition
     * @param errorHandler
     */
    public void postprocess(RegistryDefinition registryDefinition, ErrorHandler errorHandler);

}
