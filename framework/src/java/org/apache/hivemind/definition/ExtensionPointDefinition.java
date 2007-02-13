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

import org.apache.hivemind.Locatable;

/**
 * Defines an extension point of a module.
 * 
 * @author Huegen
 */
public interface ExtensionPointDefinition extends Locatable
{
    /**
     * @return the id of the module that defined this extension point
     */
    public String getModuleId();

    /**
     * @return  the id of the extension point (unqualified, without module id)
     */
    public String getId();

    /**
     * @return  the qualifed id of the extension point (includes module id)
     */
    public String getQualifiedId();

    /**
     * @return  the visibility of the extension point
     */
    public Visibility getVisibility();

}