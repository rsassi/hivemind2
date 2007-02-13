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

/**
 * 
 */
package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.Orderable;
import org.apache.hivemind.definition.InterceptorConstructor;
import org.apache.hivemind.definition.ModuleDefinition;

/**
 * Specialization of {@link ServiceInterceptorDefinitionImpl} that implements the {@link Orderable}
 * interface.
 */
public class OrderedServiceInterceptorDefinitionImpl extends ServiceInterceptorDefinitionImpl implements Orderable
{

    private String _precedingInterceptorIds;
    private String _followingInterceptorIds;

    public OrderedServiceInterceptorDefinitionImpl(ModuleDefinition module, String name, Location location, InterceptorConstructor interceptorConstructor,
            String precedingInterceptorIds, String followingInterceptorIds)
    {
        super(module, name, location, interceptorConstructor);
        _precedingInterceptorIds = precedingInterceptorIds;
        _followingInterceptorIds = followingInterceptorIds;
    }

    public String getFollowingNames()
    {
        return _followingInterceptorIds;
    }

    public String getPrecedingNames()
    {
        return _precedingInterceptorIds;
    }

}