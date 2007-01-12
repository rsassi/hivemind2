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

package org.apache.hivemind.events;

import java.util.EventListener;

import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Lifecycle interface that may be implemented by objects
 * that need to know when the {@link org.apache.hivemind.Registry}
 * has been fully initialized and that need a reference to the {@link RegistryInfrastructure} interface.
 * Typically, this is implemented by core service implementations.
 * 
 * @author Achim Huegen
 */
public interface RegistryInitializationListener extends EventListener
{
	/**
	 * Invoked when the registry has been initialized.
     * All runtime data (Modules, ServicePoints) etc. is assembled.
     * The hivemind.Startup service point has not been launched before.   
	 */
	public void registryInitialized(RegistryInfrastructure registry);
}
