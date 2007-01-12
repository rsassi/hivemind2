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

import java.io.Serializable;
import java.lang.reflect.Modifier;

import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ser.ServiceSerializationHelper;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Class used to assist extension points in creating proxies.
 * 
 * @author Howard Lewis Ship
 */
public final class ProxyBuilder
{
    private Class _serviceInterface;

    private ClassFab _classFab;

    private String _type;

    /**
     * Constructs a new builder. The type will be incorporated into value returned by the
     * <code>toString()</code> method. 
     * The generated proxy has an constructor that expects the extension point id as single parameter. 
     * 
     * @param type
     *            used as part of the <code>toString()</code> method's return value
     * @param module
     *            the module that constructs the proxy 
     * @param serviceInterface
     *            the interface of the proxied class.  
     * @param declaredInterface
     *            the interface of the proxied class or the class itself if the proxied class is actually a POJO.
     * @param outerProxy
     *            if false, then the proxy can extend the configuration points service interface always.
     *            If true and the declared interface is actually a bean class (not
     *            an interface), then the proxy will be a subclass of that bean.
     */
    public ProxyBuilder(String type, Module module, Class serviceInterface, Class declaredInterface, boolean outerProxy)
    {
        _type = type;
        _serviceInterface = serviceInterface;

        ClassFactory factory = (ClassFactory) module.getService(
                "hivemind.ClassFactory",
                ClassFactory.class);

        boolean extendBeanClass = outerProxy && !declaredInterface.isInterface();
        Class baseClass = extendBeanClass ? declaredInterface : Object.class;

        _classFab = factory.newClass(ClassFabUtils.generateClassName(_serviceInterface), baseClass);

        _classFab.addField("_extensionPointId", String.class);
        
        _classFab.addConstructor(new Class[] { String.class }, null, "{ _extensionPointId = $1; }");
        
        if (!extendBeanClass)
            _classFab.addInterface(_serviceInterface);

        // Not exactly certain this will work with non-interface beans that already
        // are serializable!

        if (outerProxy)
            addSerializable();
    }

    /** @since 1.1 */
    private void addSerializable()
    {
        _classFab.addInterface(Serializable.class);

        BodyBuilder bb = new BodyBuilder();

        bb.add(
                "return {0}.getServiceSerializationSupport().getServiceTokenForService(_extensionPointId);",
                ServiceSerializationHelper.class.getName());

        MethodSignature sig = new MethodSignature(Object.class, "writeReplace", null, null);

        _classFab.addMethod(Modifier.PRIVATE, sig, bb.toString());
    }

    public ClassFab getClassFab()
    {
        return _classFab;
    }
    
    /**
     * @see #addServiceMethods(String, boolean)
     */
    public void addServiceMethods(String indirection)
    {
        addServiceMethods(indirection, true);
    }

    /**
     * Creates the service methods for the class.
     * 
     * @param indirection
     *            the name of a variable, or a method invocation snippet, used to redirect the
     *            invocation on the proxy to the actual service implementation.
     * @param addToString if true, a implementation of the toString method is generated that
     *    returns some info about the proxy
     */
    public void addServiceMethods(String indirection, boolean addToString)
    {
        BodyBuilder builder = new BodyBuilder();

        MethodIterator mi = new MethodIterator(_serviceInterface);
        while (mi.hasNext())
        {
            MethodSignature m = mi.next();
            if( !_classFab.containsMethod( m ) )
            {
                builder.clear();
                builder.begin();
                builder.add("return ($r) ");
                builder.add(indirection);
                builder.add(".");
                builder.add(m.getName());
                builder.addln("($$);");
                builder.end();
                _classFab.addMethod(Modifier.PUBLIC, m, builder.toString());
            }
        }

        if (!mi.getToString() && addToString)
            ClassFabUtils.addToStringMethod(_classFab, "<" + _type + " for \" + _extensionPointId + \""
                    + "(" + _serviceInterface.getName() + ")>");
    }
}