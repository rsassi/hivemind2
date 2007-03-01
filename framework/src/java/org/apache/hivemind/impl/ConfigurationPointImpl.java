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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionContext;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.impl.servicemodel.SingletonInnerProxy;
import org.apache.hivemind.internal.AbstractConstructionContext;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.BodyBuilder;
import org.apache.hivemind.service.ClassFab;
import org.apache.hivemind.service.MethodSignature;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Implementation of the {@link org.apache.hivemind.internal.ConfigurationPoint} interface; a
 * container for {@link org.apache.hivemind.definition.Contribution}s.
 * 
 * @author Howard Lewis Ship
 */
public final class ConfigurationPointImpl extends AbstractExtensionPoint implements
        ConfigurationPoint
{
    private static final Log LOG = LogFactory.getLog(ConfigurationPointImpl.class);

    /**
     * The cached elements for the extension point (if caching is enabled).
     */
    private Object _configuration;
    
    private Class _configurationInterface;

    private Object _configurationProxy;

    private Occurances _expectedCount;

    private boolean _building;

    // TODO: use ShutdownCoordinator 
    private ShutdownCoordinator _shutdownCoordinator;

    public ConfigurationPointImpl(Module module, ConfigurationPointDefinition definition)
    {
        super(module, definition);
    }

    public ConfigurationPointDefinition getConfigurationPointDefinition()
    {
        return (ConfigurationPointDefinition) super.getDefinition();
    }
    
    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("type", getConfigurationTypeName());
        builder.append("expectedCount", _expectedCount);
    }
    
    public Collection getContributions()
    {
        return getConfigurationPointDefinition().getContributions();
    }

    /**
     * Returns the number of contributions; it is expected that each top-level
     * {@link org.apache.hivemind.Element} in each {@link Contribution} will convert to one element
     * instance; the value returned is the total number of top-level elements in all contributed
     * Extensions.
     */
    public int getContributionCount()
    {
        if (getConfigurationPointDefinition() == null)
            return 0;

        return getContributions().size();
    }

    public Occurances getExpectedCount()
    {
        return getConfigurationPointDefinition().getExpectedContributions();
    }

    /**
     * @return true if configuration should be created lazy, that means a proxy must be created.
     */
    public boolean isLazy()
    {
        // exclude ServiceModels, otherwise a cycle occurs because the proxy generation
        // requires the {@link ClassFactory service}
        // non interface types are not supported because this gets just too expensive
        // in terms of cpu and memory: 3 proxy classes would be needed
        return !getExtensionPointId().equals("hivemind.ServiceModels") && 
          getConfigurationType().isInterface() &&
          getConfigurationPointDefinition().isLazy();
    }

    /**
     * @see org.apache.hivemind.internal.ConfigurationPoint#getConfiguration()
     */
    public synchronized Object getConfiguration()
    {
        if (_configuration != null)
            return _configuration;

        if (isLazy()) {
            // Configuration is lazy, so return a proxy that generates the configuration
            // the first time a member method is called
            if (_configurationProxy == null)
            {
                _configurationProxy = createSingletonProxy();
            }
            return _configurationProxy;
            
        } else {
            // construct the container immediately
            _configuration = constructConfiguration();
            return _configuration;
        }
    }

    /**
     * Called by the proxy responsible for lazy construction of the configuration when 
     * the first time a method of the container proxy is called. 
     * Generates the real configuration object and stores it in a field.
     * Must be public so the proxy can access it.
     */
    public synchronized Object constructConfiguration()
    {
        // It's nice to have this protection, but (unlike services), you
        // would really have to go out of your way to provoke
        // a recursive configuration.

        if (_building)
            throw new ApplicationRuntimeException(ImplMessages
                    .recursiveConfiguration(getExtensionPointId()));

        try
        {
            if (_configuration == null)
            {
                _building = true;
                
                processContributions();
            }

            // Now that we have the real list, we don't need the proxy anymore, either.

            _configurationProxy = null;

            return _configuration;
        }
        finally
        {
            _building = false;
        }
    }

    /**
     * Adds all contributions to the configuration container.
     */
    private void processContributions()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Constructing extension point " + getExtensionPointId());

        Collection contributions = getContributions();
        
        if (contributions == null)
            return;

        try
        {
            for (Iterator iterContrib = contributions.iterator(); iterContrib.hasNext();)
            {
                ContributionDefinition cd = (ContributionDefinition) iterContrib.next();
                Module definingModule = getModule().getRegistry().getModule(cd.getModuleId());
                ContributionContext context = new ContributionContextImpl(definingModule, this);
                cd.getContribution().contribute(context);
            }
            // For backward compatibility create empty collections if nothing was contributed
            if (_configuration == null) {
                initEmptyCollection();
            }
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ImplMessages.unableToConstructConfiguration(
                    getExtensionPointId(),
                    ex), ex);
        }

    }
    
    /**
     * Implementation of {@link ContributionContext}.
     * Currently defined inline since it needs access to private methods of the outer configuration point. 
     */
    class ContributionContextImpl extends AbstractConstructionContext implements ContributionContext 
    {
        private ConfigurationPoint _configurationPoint;

        public ContributionContextImpl(Module definingModule, ConfigurationPoint configurationPoint)
        {
            super(definingModule);
            _configurationPoint = configurationPoint;
        }

        public Object getConfigurationData()
        {
            return _configuration;
        }

        public void mergeContribution(Object contributionData)
        {
            ConfigurationPointImpl.this.mergeContribution(contributionData);
        }

        public void setConfigurationData(Object data)
        {
            _configuration = data;
        }

        public ConfigurationPoint getConfigurationPoint()
        {
            return _configurationPoint;
        }
    }
    
    private void initEmptyCollection()
    {
        // TODO: this should be xml specific and maybe realized as initial empty contribution?
        // But what happens with the contribution count?
        // Move contribution count to xml?
        if (List.class.equals(getConfigurationType())) {
            _configuration = new ArrayList();
        }
        else if (Map.class.equals(getConfigurationType())) {
            _configuration = new HashMap(); 
        }
    }

    /**
     * Merges a contribution with the configuration data already present in the field _configuration.
     * TODO: Refactor as configurable service
     * @param contribution
     */
    private void mergeContribution(Object contribution)
    {
        if (!getConfigurationType().isAssignableFrom(contribution.getClass())) {
            throw new ApplicationRuntimeException("contribution of of type " +
                    contribution.getClass().getName() + " is not compatible to configuration type " + 
                    getConfigurationType().getName());
        }
        if (_configuration == null) {
            _configuration = contribution;
        } else {
            if (_configuration instanceof Collection) {
                ((Collection) _configuration).addAll((Collection) contribution); 
            }
            else if (_configuration instanceof Map) {
                ((Map) _configuration).putAll((Map) contribution); 
            }
        }
        
        
    }

    public void setShutdownCoordinator(ShutdownCoordinator coordinator)
    {
        _shutdownCoordinator = coordinator;
    }

    public String getConfigurationTypeName()
    {
        return getConfigurationPointDefinition().getConfigurationTypeName();
    }

    /**
     * @see org.apache.hivemind.internal.ConfigurationPoint#getConfigurationType()
     */
    public Class getConfigurationType()
    {
        if (_configurationInterface == null)
            _configurationInterface = getModule().resolveType(getConfigurationTypeName());

        return _configurationInterface;
    }

    /**
     * Creates a proxy class for the service and then construct the class itself.
     */
    private Object createSingletonProxy()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Creating LazyConstructionProxy for configuration "
                    + getExtensionPointId());

        try
        {

            // Create the outer proxy, the one visible to client code (including
            // other services). It is dependent on an inner proxy.

            Class proxyClass = getSingletonProxyClass();

            // Create the inner proxy, whose job is to replace itself
            // when the first service method is invoked.

            Class innerProxyClass = getInnerProxyClass(proxyClass);

            // Create the outer proxy.

            Constructor co = proxyClass.getConstructor(new Class[]
            { String.class });

            Object result = co.newInstance(new Object[] { getExtensionPointId() });

            // The inner proxy's construct invokes a method on the
            // outer proxy to connect the two.

            Constructor ci = innerProxyClass.getConstructor(new Class[]
            { String.class, proxyClass, getClass() });

            ci.newInstance(new Object[] { getExtensionPointId(), result, this });

            return result;
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex);
        }

    }
    
    private final static Map SINGLETON_PROXY_CACHE = new HashMap();
    private final static Map INNER_PROXY_CACHE = new HashMap();
    
    private Class getSingletonProxyClass()
    {
        Class configurationInterface = getConfigurationType();
        Class result = (Class) SINGLETON_PROXY_CACHE.get(configurationInterface);
        if (result == null) {
          result = createSingletonProxyClass();
          SINGLETON_PROXY_CACHE.put(configurationInterface, result);
        }
        return result;
    }
    
    private Class getInnerProxyClass(Class deferredProxyClass)
    {
        Class result = (Class) INNER_PROXY_CACHE.get(deferredProxyClass);
        if (result == null) {
          result = createInnerProxyClass(deferredProxyClass);
          INNER_PROXY_CACHE.put(deferredProxyClass, result);
        }
        return result;
    }

    /**
     * Creates a class that implements the service interface. Implements a private synchronized
     * method, _configuration(), that constructs the service as needed, and has each service interface
     * method re-invoke on _configuration(). Adds a toString() method if the service interface does not
     * define toString().
     */
    private Class createSingletonProxyClass()
    {
        ProxyBuilder proxyBuilder = new ProxyBuilder("LazyConstructionProxy", getModule(), getConfigurationType(), 
                getConfigurationType(), true);

        ClassFab classFab = proxyBuilder.getClassFab();


        // This will initally be the inner proxy, then switch over to the
        // service implementation.

        classFab.addField("_inner", getConfigurationType());
        classFab.addMethod(
                Modifier.PUBLIC | Modifier.SYNCHRONIZED | Modifier.FINAL,
                new MethodSignature(void.class, "_setInner", new Class[]
                { getConfigurationType() }, null),
                "{ _inner = $1; }");

        BodyBuilder builder = new BodyBuilder();
        builder.begin();

        builder.addln("return _inner;");
        builder.end();

        classFab.addMethod(Modifier.PRIVATE, new MethodSignature(getConfigurationType(), "_getInner",
                null, null), builder.toString());

        proxyBuilder.addServiceMethods("_getInner()", false);

        // The toString calls the toString method of the configuration if it is
        // created already
        // TODO: Implement like described
//        String proxyToStringMessage = "<LazyConstructionProxy for "
//            + getExtensionPointId() + "(" + configurationInterface.getName() + ")>";
        builder.clear();
        builder.begin();
        builder.addln(" return _inner.toString();");
        builder.end();

        MethodSignature toStringSignature = new MethodSignature(String.class, "toString", null,
                null);
        if (!classFab.containsMethod(toStringSignature)) {
            classFab.addMethod(Modifier.PUBLIC, toStringSignature, builder.toString());
        }

        return classFab.createClass();
    }

    private Class createInnerProxyClass(Class deferredProxyClass)
    {
        ProxyBuilder builder = new ProxyBuilder("InnerProxy", getModule(), getConfigurationType(), 
                getConfigurationType(), false);

        ClassFab classFab = builder.getClassFab();

        classFab.addField("_deferredProxy", deferredProxyClass);
        classFab.addField("_configuration", getConfigurationType());
        classFab.addField("_configurationPoint", ConfigurationPointImpl.class);

        BodyBuilder body = new BodyBuilder();

        // The constructor remembers the outer proxy and registers itself
        // with the outer proxy.

        body.begin();

        body.addln("this($1);");
        body.addln("_deferredProxy = $2;");
        body.addln("_configurationPoint = $3;");
        body.addln("_deferredProxy._setInner(this);");

        body.end();

        classFab.addConstructor(new Class[]
        { String.class, deferredProxyClass, ConfigurationPointImpl.class }, null, body.toString());

        // Method _configuration() will look up the configuration,
        // then update the deferred proxy to go directly to the
        // configuration, bypassing itself!

        body.clear();
        body.begin();

        body.add("if (_configuration == null)");
        body.begin();

        body.add("_configuration = (");
        body.add(getConfigurationType().getName());
        body.addln(") _configurationPoint.constructConfiguration();");

        body.add("_deferredProxy._setInner(_configuration);");

        body.end();

        body.add("return _configuration;");

        body.end();

        classFab.addMethod(
                Modifier.PRIVATE | Modifier.FINAL | Modifier.SYNCHRONIZED,
                new MethodSignature(getConfigurationType(), "_configuration", null, null),
                body.toString());

        builder.addServiceMethods("_configuration()");

        // Build the implementation of interface SingletonInnerProxy

        body.clear();
        body.begin();

        body.add("_configuration();");

        body.end();

        classFab.addMethod(Modifier.PUBLIC | Modifier.FINAL, new MethodSignature(void.class,
                "_instantiateServiceImplementation", null, null), body.toString());

        classFab.addInterface(SingletonInnerProxy.class);

        return classFab.createClass();
    }

}