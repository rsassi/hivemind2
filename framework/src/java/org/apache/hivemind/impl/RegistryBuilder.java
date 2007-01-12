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

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Registry;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.events.RegistryInitializationListener;
import org.apache.hivemind.internal.RegistryInfrastructure;

/**
 * Class used to build a {@link org.apache.hivemind.Registry} from individual
 * {@link org.apache.hivemind.definition.RegistryDefinition}. 
 * A note about threadsafety: The assumption is that a single thread will access the RegistryBuilder
 * at one time (typically, a startup class within some form of server or application). Code here and
 * in many of the related classes is divided into construction-time logic and runtime logic. Runtime
 * logic is synchronized and threadsafe. Construction-time logic is not threadsafe. Once the
 * registry is fully constructed, it is not allowed to invoke those methods (though, at this time,
 * no checks occur).
 * <p>
 * Runtime methods, such as {@link org.apache.hivemind.impl.ModuleImpl#getService(String, Class)}
 * are fully threadsafe.
 * 
 * @author Howard Lewis Ship
 */
public final class RegistryBuilder
{
    private static final Log LOG = LogFactory.getLog(RegistryBuilder.class);

    static
    {
        if (!LOG.isErrorEnabled())
        {
            System.err
                    .println("********************************************************************************");
            System.err
                    .println("* L O G G I N G   C O N F I G U R A T I O N   E R R O R                        *");
            System.err
                    .println("* ---------------------------------------------------------------------------- *");
            System.err
                    .println("* Logging is not enabled for org.apache.hivemind.impl.RegistryBuilder.         *");
            System.err
                    .println("* Errors during HiveMind module descriptor parsing and validation may not be   *");
            System.err
                    .println("* logged. This may result in difficult-to-trace runtime exceptions, if there   *");
            System.err
                    .println("* are errors in any of your module descriptors. You should enable error        *");
            System.err
                    .println("* logging for the org.apache.hivemind and hivemind loggers.                    *");
            System.err
                    .println("********************************************************************************");
        }
    }

    /**
     * Delegate used for handling errors.
     */

    private ErrorHandler _errorHandler;

    private RegistryDefinition _registryDefinition;

    public RegistryBuilder()
    {
        this(new RegistryDefinition(), new DefaultErrorHandler());
    }
    
    public RegistryBuilder(RegistryDefinition registryDefinition)
    {
        this(registryDefinition, new DefaultErrorHandler());
    }

    public RegistryBuilder(ErrorHandler errorHandler)
    {
        this(new RegistryDefinition(), errorHandler);
    }
    
    public RegistryBuilder(RegistryDefinition registryDefinition, ErrorHandler errorHandler)
    {
        _registryDefinition = registryDefinition;
        _errorHandler = errorHandler;
    }

    public RegistryDefinition getRegistryDefinition()
    {
        return _registryDefinition;
    }
 
    /**
     * Constructs the registry from its {@link RegistryDefinition}. Default locale is used.
     * @see #constructRegistry(Locale)
     */
    public Registry constructRegistry()
    {
        return constructRegistry();
    }
    
    /**
     * Constructs the registry from its {@link RegistryDefinition}.
     */
    public Registry constructRegistry(Locale locale)
    {
        // Add Core HiveMind services like ClassFactory
        CoreServicesProvider coreServicesProvider = new CoreServicesProvider();
        coreServicesProvider.process(_registryDefinition, _errorHandler);
        
        // Try to resolve all so far unresolved extensions
        ExtensionResolver extensionResolver = new ExtensionResolver(_registryDefinition, _errorHandler);
        extensionResolver.resolveExtensions();
        
        checkDependencies(_registryDefinition);
        checkContributionCounts(_registryDefinition);
        
        // Notify post processors
        for (Iterator i = _registryDefinition.getPostProcessors().iterator(); i.hasNext();)
        {
            RegistryPostProcessor processor = (RegistryPostProcessor) i.next();

            processor.postprocess(_registryDefinition, _errorHandler);
        }

        RegistryInfrastructureConstructor constructor = new RegistryInfrastructureConstructor(_errorHandler, LOG, locale);
        RegistryInfrastructure infrastructure = constructor
                .constructRegistryInfrastructure(_registryDefinition);

        // Notify initialization listeners
        for (Iterator i = _registryDefinition.getRegistryInitializationListeners().iterator(); i.hasNext();)
        {
            RegistryInitializationListener listener = (RegistryInitializationListener) i.next();

            listener.registryInitialized(infrastructure);
        }
        
        infrastructure.startup();

        return new RegistryImpl(infrastructure);
    }
    
    /**
     * Constructs the registry from a specified {@link RegistryDefinition}.
     */
    public static Registry constructRegistry(RegistryDefinition definition, ErrorHandler errorHandler,
            Locale locale)
    {
        RegistryBuilder builder = new RegistryBuilder(definition, errorHandler);
        return builder.constructRegistry(locale);
    }

    private void checkDependencies(RegistryDefinition definition)
    {
        for (Iterator iterModules = definition.getModules().iterator(); iterModules.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) iterModules.next();
            
            for (Iterator iterDependencies = module.getDependencies().iterator(); iterDependencies.hasNext();)
            {
                String requiredModuleId = (String) iterDependencies.next();
                checkModuleDependency(definition, module, requiredModuleId);
            }
        }
        
    }

    private void checkModuleDependency(RegistryDefinition definition, ModuleDefinition sourceModule, String requiredModuleId)
    {
        ModuleDefinition requiredModule = (ModuleDefinition) definition.getModule(requiredModuleId);
        if (requiredModule == null)
        {
            // TODO annotation: Location in Dependencies aufnehmen
            _errorHandler.error(
                    LOG,
                    DefinitionMessages.dependencyOnUnknownModule(requiredModuleId),
                    null,
                    null);
            return;
        }
    }
    
    /**
     * Checks that each configuration extension point has the right number of contributions.
     */
    public void checkContributionCounts(RegistryDefinition definition)
    {
        for (Iterator iterModules = definition.getModules().iterator(); iterModules.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) iterModules.next();
            
            for (Iterator iterConfigurations = module.getConfigurationPoints().iterator(); iterConfigurations.hasNext();)
            {
                ConfigurationPointDefinition cpd = (ConfigurationPointDefinition) iterConfigurations.next();
                checkContributionCounts(module, cpd);
            }
        }
    }
    
    private void checkContributionCounts(ModuleDefinition definingModule, ConfigurationPointDefinition configurationPoint)
    {
        Occurances expected = configurationPoint.getExpectedContributions();

        int actual = configurationPoint.getContributions().size();

        if (expected.inRange(actual))
            return;

        _errorHandler.error(LOG, DefinitionMessages.wrongNumberOfContributions(
                definingModule, configurationPoint,
                actual,
                expected), configurationPoint.getLocation(), null);
    }

    /**
     * Automatically loads hivemind modules on the classpath which are 
     * provided by {@link RegistryProvider}s which are defined in Manifest-Files. 
     */
    public void autoDetectModules()
    {
        RegistryProviderAutoDetector detector = new RegistryProviderAutoDetector(new DefaultClassResolver());
        List providers = detector.getProviders();
        for (Iterator iterProviders = providers.iterator(); iterProviders.hasNext();)
        {
            RegistryProvider provider = (RegistryProvider) iterProviders.next();
            provider.process(getRegistryDefinition(), _errorHandler);
        }
    }

    /**
     * Constructs a default registry based on just the modules visible to the thread context class
     * loader (this is sufficient is the majority of cases), and using the default locale. If you
     * have different error handling needs, or wish to pick up HiveMind module deployment
     * descriptors for non-standard locations, you must create a RegistryBuilder instance yourself.
     * 
     * @see #addDefaultModuleDescriptorProvider()
     */
    public static Registry constructDefaultRegistry()
    {
        RegistryBuilder builder = new RegistryBuilder();
        builder.autoDetectModules();
        return builder.constructRegistry(Locale.getDefault());
    }

    public ErrorHandler getErrorHandler()
    {
        return _errorHandler;
    }

}