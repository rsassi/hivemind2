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

import org.apache.hivemind.internal.ServiceModel;

/**
 * Defines an implementation of a {@link ServicePointDefinition service point}. 
 * The implementation instance is created by a {@link ImplementationConstructor}.
 * 
 * @author Huegen
 */
public interface ImplementationDefinition extends ExtensionDefinition
{
    /**
     * @return the name of the {@link ServiceModel} to use 
     */
    public String getServiceModel();

    /**
     * @return  the constructor for the creation of the implementation instance.
     */
    public ImplementationConstructor getServiceConstructor();

    /**
     * @return  true, if this is the default implementation of the service
     */
    public boolean isDefault();

}