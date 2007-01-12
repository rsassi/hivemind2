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

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.methodmatch.MethodMatcher;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.ClassFabUtils;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.MethodContribution;
import org.apache.hivemind.service.MethodFab;
import org.apache.hivemind.service.MethodIterator;
import org.apache.hivemind.service.MethodSignature;

/**
 * Factory for creation of interceptor classes that add logging capability to a service.
 * The logging is based upon the Jakarta 
 * <a href="http://jakarta.apache.org/commons/logging.html">commons-logging</a> toolkit, 
 * which makes it very transportable.
 * 
 * <p>
 * The interceptor will log entry to each method and exit from the method
 * (with return value), plus log any exceptions thrown by the method.
 * The logger used is the <em>id of the service</em>, which is not necessarily
 * the name of the implementing class.  Logging occurs at the debug level.
 *
 * @author Howard Lewis Ship
 */
public class LoggingInterceptorClassFactory 
{
    private ClassFactory _factory;

    public LoggingInterceptorClassFactory(ClassFactory factory)
    {
        _factory = factory;
    }

    /**
     * Creates a method that delegates to the _delegate object; this is used for
     * methods that are not logged.
     */
    private void addPassThruMethodImplementation(ClassFab classFab, MethodSignature sig)
    {
        BodyBuilder builder = new BodyBuilder();
        builder.begin();

        builder.add("return ($r) _delegate.");
        builder.add(sig.getName());
        builder.addln("($$);");

        builder.end();

        classFab.addMethod(Modifier.PUBLIC, sig, builder.toString());
    }

    protected void addServiceMethodImplementation(ClassFab classFab, MethodSignature sig)
    {
        Class returnType = sig.getReturnType();
        String methodName = sig.getName();

        boolean isVoid = (returnType == void.class);

        BodyBuilder builder = new BodyBuilder();

        builder.begin();
        builder.addln("boolean debug = _log.isDebugEnabled();");

        builder.addln("if (debug)");
        builder.add("  org.apache.hivemind.service.impl.LoggingUtils.entry(_log, ");
        builder.addQuoted(methodName);
        builder.addln(", $args);");

        if (!isVoid)
        {
            builder.add(ClassFabUtils.getJavaClassName(returnType));
            builder.add(" result = ");
        }

        builder.add("_delegate.");
        builder.add(methodName);
        builder.addln("($$);");

        if (isVoid)
        {
            builder.addln("if (debug)");
            builder.add("  org.apache.hivemind.service.impl.LoggingUtils.voidExit(_log, ");
            builder.addQuoted(methodName);
            builder.addln(");");
        }
        else
        {
            builder.addln("if (debug)");
            builder.add("  org.apache.hivemind.service.impl.LoggingUtils.exit(_log, ");
            builder.addQuoted(methodName);
            builder.addln(", ($w)result);");
            builder.addln("return result;");
        }

        builder.end();

        MethodFab methodFab = classFab.addMethod(Modifier.PUBLIC, sig, builder.toString());

        builder.clear();

        builder.begin();
        builder.add("org.apache.hivemind.service.impl.LoggingUtils.exception(_log, ");
        builder.addQuoted(methodName);
        builder.addln(", $e);");
        builder.addln("throw $e;");
        builder.end();

        String body = builder.toString();

        Class[] exceptions = sig.getExceptionTypes();

        int count = exceptions.length;

        for (int i = 0; i < count; i++)
        {
            methodFab.addCatch(exceptions[i], body);
        }

        // Catch and log any runtime exceptions, in addition to the
        // checked exceptions.

        methodFab.addCatch(RuntimeException.class, body);
    }

    protected void addServiceMethods(InterceptorStack stack, ClassFab fab, List parameters)
    {
        MethodMatcher matcher = buildMethodMatcher(parameters);

        MethodIterator mi = new MethodIterator(stack.getServiceInterface());

        while (mi.hasNext())
        {
            MethodSignature sig = mi.next();

            if (includeMethod(matcher, sig))
                addServiceMethodImplementation(fab, sig);
            else
                addPassThruMethodImplementation(fab, sig);
        }

        if (!mi.getToString())
            addToStringMethod(stack, fab);
    }

    /**
     * Creates a toString() method that identify the interceptor service id,
     * the intercepted service id, and the service interface class name).
     */
    protected void addToStringMethod(InterceptorStack stack, ClassFab fab)
    {
        ClassFabUtils.addToStringMethod(
            fab,
            "<LoggingInterceptor for "
                + stack.getServiceExtensionPointId()
                + "("
                + stack.getServiceInterface().getName()
                + ")>");

    }

    private MethodMatcher buildMethodMatcher(List parameters)
    {
        MethodMatcher result = null;

        Iterator i = parameters.iterator();
        while (i.hasNext())
        {
            MethodContribution mc = (MethodContribution) i.next();

            if (result == null)
                result = new MethodMatcher();

            result.put(mc.getMethodPattern(), mc);
        }

        return result;
    }

    /**
     * Creates the interceptor class.
     */
    public Class constructInterceptorClass(InterceptorStack stack, List parameters)
    {
        Class serviceInterfaceClass = stack.getServiceInterface();
        
        String name = ClassFabUtils.generateClassName(serviceInterfaceClass);

        ClassFab classFab = _factory.newClass(name, Object.class);

        classFab.addInterface(serviceInterfaceClass);

        createInfrastructure(stack, classFab);

        addServiceMethods(stack, classFab, parameters);

        return classFab.createClass();
    }

    private void createInfrastructure(InterceptorStack stack, ClassFab classFab)
    {
        Class topClass = ClassFabUtils.getInstanceClass(classFab, stack.peek(), stack.getServiceInterface());
        
        classFab.addField("_log", Log.class);

        // This is very important: since we know the instance of the top object (the next
        // object in the pipeline for this service), we can build the instance variable
        // and constructor to use the exact class rather than the service interface.
        // That's more efficient at runtime, lowering the cost of using interceptors.
        // One of the reasons I prefer Javassist over JDK Proxies.

        classFab.addField("_delegate", topClass);

        classFab.addConstructor(
            new Class[] { Log.class, topClass },
            null,
            "{ _log = $1; _delegate = $2; }");
    }

    private boolean includeMethod(MethodMatcher matcher, MethodSignature sig)
    {
        if (matcher == null)
            return true;

        MethodContribution mc = (MethodContribution) matcher.get(sig);

        return mc == null || mc.getInclude();
    }

}
