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

package org.apache.hivemind.impl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.definition.ServiceInterceptorDefinition;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of the {@link org.apache.hivemind.InterceptorStack} interface; localizes
 * error checking in one place.
 *
 * @author Howard Lewis Ship
 */
public final class InterceptorStackImpl implements InterceptorStack
{
    private final Log _log;

    private ServiceInterceptorDefinition _interceptorDefinition;
    private ServicePoint _sep;
    private Class _interfaceClass;
    private Object _top;

    public InterceptorStackImpl(Log log, ServicePoint sep, Object root)
    {
        _log = log;
        _sep = sep;
        _top = root;
        _interfaceClass = sep.getServiceInterface();
    }

    public String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("contribution", _interceptorDefinition);
        builder.append("interfaceClass", _interfaceClass);
        builder.append("top", _top);

        return builder.toString();
    }

    public String getServiceExtensionPointId()
    {
        return _sep.getExtensionPointId();
    }

    public Module getServiceModule()
    {
        return _sep.getModule();
    }

    public Class getServiceInterface()
    {
        return _interfaceClass;
    }

    public Object peek()
    {
        return _top;
    }

    public void push(Object interceptor)
    {
        if (interceptor == null)
            throw new ApplicationRuntimeException(
                ImplMessages.nullInterceptor(_interceptorDefinition, _sep),
                _interceptorDefinition.getLocation(),
                null);

        if (!_interfaceClass.isAssignableFrom(interceptor.getClass()))
            throw new ApplicationRuntimeException(
                ImplMessages.interceptorDoesNotImplementInterface(
                    interceptor,
                    _interceptorDefinition,
                    _sep,
                    _interfaceClass),
                _interceptorDefinition.getLocation(),
                null);

        _top = interceptor;
    }

    /**
     * Invoked to process the next interceptor contribution; these should
     * be processed in ascending order.
     * 
     */

    public void process(ServiceInterceptorDefinition interceptorDefinition)
    {
        if (_log.isDebugEnabled())
            _log.debug("Applying interceptor factory " + interceptorDefinition.getName());

        // And now we can finally do this!
        try
        {
            _interceptorDefinition = interceptorDefinition;
            Module contributingModule = getServiceModule().getRegistry().getModule(interceptorDefinition.getModuleId());
            _interceptorDefinition.getInterceptorConstructor().constructServiceInterceptor(this, contributingModule);
        }
        finally
        {
            _interceptorDefinition = null;
        }
    }

	public Log getServiceLog()
	{
		return _log;
	}
}
