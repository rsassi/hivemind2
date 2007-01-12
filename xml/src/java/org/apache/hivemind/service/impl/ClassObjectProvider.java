// Copyright 2005 The Apache Software Foundation
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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ObjectProvider;

/**
 * Implementation of {@link org.apache.hivemind.service.ObjectProvider} that interprets the locator
 * as a class name and converts it to a Class object. The namemay be partial; it is interpreted
 * using the contributing module's package name.  Mapped to the "class:" prefix.
 * 
 * @author Howard M. Lewis Ship
 * @since 1.1
 */
public class ClassObjectProvider implements ObjectProvider
{

    public Object provideObject(Module contributingModule, Class propertyType, String locator,
            Location location)
    {
        return contributingModule.resolveType(locator);
    }

}