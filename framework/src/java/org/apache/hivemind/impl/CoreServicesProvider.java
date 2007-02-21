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

package org.apache.hivemind.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.ShutdownCoordinator;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionContext;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionHelper;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.impl.servicemodel.PooledServiceModelFactory;
import org.apache.hivemind.impl.servicemodel.PrimitiveServiceModelFactory;
import org.apache.hivemind.impl.servicemodel.SingletonServiceModelFactory;
import org.apache.hivemind.impl.servicemodel.ThreadedServiceModelFactory;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.service.Autowiring;
import org.apache.hivemind.service.AutowiringStrategy;
import org.apache.hivemind.service.ClassFactory;
import org.apache.hivemind.service.InterfaceSynthesizer;
import org.apache.hivemind.service.ThreadEventNotifier;
import org.apache.hivemind.service.ThreadLocalStorage;
import org.apache.hivemind.service.ThreadLocale;
import org.apache.hivemind.service.impl.AutowiringByTypeStrategy;
import org.apache.hivemind.service.impl.AutowiringImpl;
import org.apache.hivemind.service.impl.AutowiringStrategyContribution;
import org.apache.hivemind.service.impl.ClassFactoryImpl;
import org.apache.hivemind.service.impl.EagerLoader;
import org.apache.hivemind.service.impl.InterfaceSynthesizerImpl;
import org.apache.hivemind.service.impl.ThreadEventNotifierImpl;
import org.apache.hivemind.service.impl.ThreadLocalStorageImpl;
import org.apache.hivemind.service.impl.ThreadLocaleImpl;

/**
 * Loads the core HiveMind services into a registry definition.
 * 
 * @author Achim Huegen
 */
public class CoreServicesProvider implements RegistryProvider
{
    private ModuleDefinitionHelper helper;

    public void process(RegistryDefinition registryDefinition, ErrorHandler errorHandler)

    {
        DefaultClassResolver resolver = new DefaultClassResolver();

        // For the sake of backward compatibility add the core
        // to an existing module that may have been created by the XmlRegistryProvider
        // This way the core services and the hivemodule.xml from the xml package share 
        // the same module name "hivemind"

        ModuleDefinition md = registryDefinition.getModule("hivemind");
        if (md == null)
        {
            md = new ModuleDefinitionImpl("hivemind", HiveMind.getClassLocation(getClass(), resolver),
                    resolver, null);
            registryDefinition.addModule(md);
        }

        // The cast to ModuleDefinitionImpl is save, since we exactly now the origin
        helper = new ModuleDefinitionHelper((ModuleDefinitionImpl) md);
        
        addClassFactory(md);

        addThreadEventNotifier(md);

        addThreadLocalStorage(md);

        addThreadLocale(md);
        
        addStartup(md);

        addEagerLoad(md);
        
        addShutdownCoordinator(md);

        addInterfaceSynthesizer(md);
        
        addServiceModelConfiguration();
        
        addAutowiring(md);
        
        addAutowiringStrategiesConfiguration();
    }

    /**
     * Wrapper around Javassist used to dynamically create classes such as service interceptors.
     */
    private void addClassFactory(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("ClassFactory", ClassFactory.class.getName());
        helper.addSimpleServiceImplementation(spd, ClassFactoryImpl.class.getName(), ServiceModel.PRIMITIVE);
    }

    /**
     * Service used by other services to be alerted when a thread is cleaned up (typically, at the
     * end of a request or transaction).
     */
    private void addThreadEventNotifier(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint(
                "ThreadEventNotifier",
                ThreadEventNotifier.class.getName());
        helper.addSimpleServiceImplementation(
                spd,
                ThreadEventNotifierImpl.class.getName(),
                ServiceModel.SINGLETON);
    }

    /**
     * Service which manages a thread-local map of data items. This can be used for temporary
     * storage of information when local variables can't be used. All stored items are released when
     * the thread is cleaned up. Note: this service should be considered deprecated; use the
     * threaded service model instead.
     */
    private void addThreadLocalStorage(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint(
                "ThreadLocalStorage",
                ThreadLocalStorage.class.getName());
        helper.addSimpleServiceImplementation(spd, ThreadLocalStorageImpl.class.getName(), ServiceModel.THREADED);
    }

    /**
     * Stores the locale for the current thread. The default is determined when the Registry is
     * first constructed. This locale is used for any messages.
     */
    private void addThreadLocale(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("ThreadLocale", ThreadLocale.class.getName());

        // Define inline implementation constructor 
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {

            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                // Get the Locale from the registry
                Locale defaultLocale = context.getDefiningModule().getLocale();
                return new ThreadLocaleImpl(defaultLocale);
            }

        };

        helper.addServiceImplementation(spd, constructor, ServiceModel.THREADED);
    }
    
    /**
     * A source of event notifications for when the Registry is shutdown.
     */
    private void addShutdownCoordinator(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("ShutdownCoordinator", ShutdownCoordinator.class.getName());
        helper.addSimpleServiceImplementation(spd, ShutdownCoordinatorImpl.class.getName(), ServiceModel.SINGLETON);
    }
    
    /**
     * Service that performs eager loading of other services. This service is contributed into the hivemind.Startup configuration.
     */
    private void addEagerLoad(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("EagerLoad", Runnable.class.getName());

        // Define inline implementation constructor, that wires the EagerLoad configuration
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                EagerLoader result = new EagerLoader();
                result.setServicePoints((List) context.getConfiguration("EagerLoad"));
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.PRIMITIVE);
        
        // Configuration to which services may be contributed. The corresponding services are instantiated eagerly, as the Registry is started. 
        // The order in which services are instantiated is not specified.

        helper.addConfigurationPoint("EagerLoad", List.class.getName());
    }

    /**
     * A service which is used to bootstrap HiveMind; it obtains the hivemind.Startup configuration and runs each 
     * Runnable object or service within as the last step of the Registry construction phase.
     * Note that the execution order is arbitrary and the startup objects are NOT executed in separate threads.
     */
    private void addStartup(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("Startup", Runnable.class.getName());

        // Define inline implementation constructor, that wires the Startup configuration
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                StartupImpl result = new StartupImpl();
                result.setRunnables((List) context.getConfiguration("Startup"));
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.PRIMITIVE);
        
        // A configuration to which startup objects may be contributed (as objects or services). 
        // Startup objects must implement the java.lang.Runnable interface. Order of execution is expliclitly NOT defined.

        ConfigurationPointDefinition cpd = helper.addConfigurationPoint("Startup", List.class.getName());
        
        final List services = getDefaultStartupServices();
        helper.addContributionDefinition(cpd, new Contribution() {

            public void contribute(ContributionContext context)
            {
                List contribution = new ArrayList(); 
                for (Iterator iterServices = services.iterator(); iterServices.hasNext();)
                {
                    String serviceName = (String) iterServices.next();
                    contribution.add(context.getService(serviceName, Runnable.class));
                }
                context.mergeContribution(contribution);
            }});
    }
    
    /**
     * Defines service models, providing a name and a class for each.
     */
    private void addServiceModelConfiguration()
    {

        ConfigurationPointDefinition cpd = helper.addConfigurationPoint("ServiceModels", 
                Map.class.getName());
        
        final List serviceModels = getDefaultServiceModels();
        helper.addContributionDefinition(cpd, new Contribution() {

            public void contribute(ContributionContext context)
            {
                Map contribution = new HashMap(); 
                for (Iterator iterServiceModels = serviceModels.iterator(); iterServiceModels.hasNext();)
                {
                    ServiceModelContribution contrib = (ServiceModelContribution) iterServiceModels.next();
                    contribution.put(contrib.getName(), contrib);
                }
                context.mergeContribution(contribution);
            }});
    }
    
    /**
     * Returns default startup services for addition to "Startup" configuration.
     */
    private List getDefaultStartupServices()
    {
        List result = new ArrayList();
        result.add("hivemind.EagerLoad");
        return result;
    }

    /**
     * Returns default service Models as instances of {@link ServiceModelContribution}.
     */
    private List getDefaultServiceModels()
    {
        List result = new ArrayList();
        result.add(new ServiceModelContribution(ServiceModel.PRIMITIVE, new PrimitiveServiceModelFactory()));
        result.add(new ServiceModelContribution(ServiceModel.SINGLETON, new SingletonServiceModelFactory()));
        result.add(new ServiceModelContribution(ServiceModel.POOLED, new PooledServiceModelFactory()));
        result.add(new ServiceModelContribution(ServiceModel.THREADED, new ThreadedServiceModelFactory()));
        return result;
    }

    /**
     * Synthesizes a service interface from an ordinary JavaBean.
     */
    private void addInterfaceSynthesizer(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("InterfaceSynthesizer", InterfaceSynthesizer.class.getName());

        // Define inline implementation constructor 
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                InterfaceSynthesizerImpl result = new InterfaceSynthesizerImpl();
                // Manual wiring of the class factory
                result.setClassFactory((ClassFactory) context.getService(ClassFactory.class));
                return result;
            }
        };

        helper.addServiceImplementation(spd, constructor, ServiceModel.SINGLETON);
    }
    
    /**
      * Service that wires properties of object with services defined in the registry.  
      */
    private void addAutowiring(ModuleDefinition md)
    {
        ServicePointDefinition spd = helper.addServicePoint("Autowiring", Autowiring.class.getName());

        // Define inline implementation constructor, that wires the AutowiringStrategies configuration
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                List strategies = (List) context.getConfiguration("AutowiringStrategies");
                Autowiring result = new AutowiringImpl(context.getRegistry(), strategies, context.getDefiningModule().getErrorHandler());
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.PRIMITIVE);

    }
    
    /**
     * Defines service models, providing a name and a class for each.
     */
    private void addAutowiringStrategiesConfiguration()
    {

        ConfigurationPointDefinition cpd = helper.addConfigurationPoint("AutowiringStrategies", List.class.getName());
        
        final List serviceModels = getDefaultAutowiringStrategies();
        helper.addContributionDefinition(cpd, new Contribution() {

            public void contribute(ContributionContext context)
            {
                List contribution = new ArrayList(); 
                contribution.addAll(serviceModels);
                context.mergeContribution(contribution);
            }});
    }

    /**
     * Returns default service Models as instances of {@link AutowiringStrategyContribution}.
     */
    private List getDefaultAutowiringStrategies()
    {
        List result = new ArrayList();
        result.add(new AutowiringStrategyContribution(new AutowiringByTypeStrategy(), AutowiringStrategy.BY_TYPE, null, null));
        return result;
    }

 }
