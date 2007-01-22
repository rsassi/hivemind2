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