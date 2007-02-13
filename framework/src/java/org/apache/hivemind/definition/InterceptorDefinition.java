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

import org.apache.hivemind.Orderable;

/**
 * Defines an interceptor for the methods of a {@link ServicePointDefinition service point}.
 * The interceptor is created by an instance of {@link InterceptorConstructor}.
 * 
 * Interceptors are applied in a certain order which bases on the interceptor names. 
 * If an implementation of this interface wants to effect the order it must
 * implement the {@link Orderable} interface too.
 * 
 * @author Huegen
 */
public interface InterceptorDefinition extends ExtensionDefinition
{
    /**
     * @return  the constructor for the creation of a interceptor instance.
     */
    public InterceptorConstructor getInterceptorConstructor();

    /**
     * @return the name of the interceptor. Used for ordering.
     */
    public String getName();

}