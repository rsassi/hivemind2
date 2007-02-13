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

package org.apache.hivemind.internal;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.InterceptorConstructor;
import org.apache.hivemind.impl.BaseLocatable;

/**
 * Ancestor for implementions of {@link InterceptorConstructor}.
 * 
 * @author Achim Huegen
 */
public abstract class AbstractServiceInterceptorConstructor extends BaseLocatable implements InterceptorConstructor
{

    public AbstractServiceInterceptorConstructor(Location location)
    {
        super(location);
    }
    
    public abstract void constructServiceInterceptor(InterceptorStack interceptorStack, Module contributingModule);

}
