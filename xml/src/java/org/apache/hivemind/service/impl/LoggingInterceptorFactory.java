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

package org.apache.hivemind.service.impl;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodContribution;

/**
 * {@link ServiceInterceptorFactory} for logging interceptors that base on 
 * {@link LoggingInterceptorClassFactory}.
 *
 * @author Achim Huegen
 */
public class LoggingInterceptorFactory implements ServiceInterceptorFactory
{
    private ClassFactory _factory;
    private String _serviceId;

    /**
     * Untyped version of {@link #createInterceptor(InterceptorStack, Module, List)}.
     */
    public void createInterceptor(
        InterceptorStack stack,
        Module contributingModule,
        Object parameters)
    {
        createInterceptor(stack, contributingModule, (List) parameters);
    }

    /**
     * Creates the interceptor.
     * The class that is created is cached; if an interceptor is requested
     * for the same extension point, then the previously constructed class
     * is reused (this can happen with the threaded service model, for example,
     * when a thread-local service implementation is created for different threads).
     * @param parameters  list with instances of {@link MethodContribution}. If empty all methods are intercepted.
     */
    public void createInterceptor(
            InterceptorStack stack,
            Module contributingModule,
            List parameters)
    {
        LoggingInterceptorClassFactory classFactory = new LoggingInterceptorClassFactory(_factory);
        Class interceptorClass = classFactory.constructInterceptorClass(stack, (List) parameters);

        try
        {
            Object interceptor = instantiateInterceptor(stack, interceptorClass);

            stack.push(interceptor);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(
                ServiceMessages.errorInstantiatingInterceptor(
                    _serviceId,
                    stack,
                    interceptorClass,
                    ex),
                ex);
        }
    }

    private Object instantiateInterceptor(InterceptorStack stack, Class interceptorClass)
        throws Exception
    {
        Object stackTop = stack.peek();

        Constructor c = interceptorClass.getConstructors()[0];

        return c.newInstance(new Object[] { stack.getServiceLog(), stackTop });
    }

    public void setFactory(ClassFactory factory)
    {
        _factory = factory;
    }

    public void setServiceId(String string)
    {
        _serviceId = string;
    }
}
