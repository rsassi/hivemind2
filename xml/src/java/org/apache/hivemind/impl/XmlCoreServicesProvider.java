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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.SymbolExpander;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.TranslatorManager;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionContext;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ModuleDefinitionHelper;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.RegistryDefinitionPostProcessor;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Implementation of {@link RegistryProvider} that defines core services
 * needed by the xml implementation. Theses services can not be defined in
 * xml because that would cause recursive behaviour.
 */
public class XmlCoreServicesProvider implements RegistryProvider
{
    private static final Log LOG = LogFactory.getLog(XmlCoreServicesProvider.class);
    private ModuleDefinitionHelper helper;

    public void process(RegistryDefinition registryDefinition, ErrorHandler errorHandler)
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Adding xml core services to registry");  
        }
        
        // The "hivemind" module is available here only if the HivemoduleProvider
        // has been executed before. This order is defined in the MANIFEST.MF file of the xml module 
        // The cast is safe since the module is defined in the core.
        ModuleDefinitionImpl moduleDefinition = (ModuleDefinitionImpl) registryDefinition.getModule("hivemind");
        if (moduleDefinition == null) {
            throw new ApplicationRuntimeException("Module 'hivemind' not found.");
        }
        
        helper = new ModuleDefinitionHelper(moduleDefinition);
        
        addTranslatorManager(moduleDefinition, errorHandler);
        
        addSymbolSourcesConfiguration(moduleDefinition);
        
        // SymbolSource implementation driven by the FactoryDefaults configuration point.
        addSymbolSource(moduleDefinition, "FactoryDefaultsSymbolSource", "FactoryDefaults");
        
        // SymbolSource implementation driven by the ApplicationDefaults configuration point.
        addSymbolSource(moduleDefinition, "ApplicationDefaultsSymbolSource", "ApplicationDefaults");
        
        addSymbolExpander(moduleDefinition, errorHandler);
        
        registryDefinition.addPostProcessor(new XmlPostProcessor());
    }
    
    /**
     * @see TranslatorManager
     */
    private void addTranslatorManager(ModuleDefinition md, final ErrorHandler errorHandler)
    {
        ServicePointDefinition spd = helper.addServicePoint("TranslationManager", TranslatorManager.class.getName());

        // Define inline implementation constructor, that wires the Translators configuration
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                List translators = (List) context.getConfiguration("Translators");
                TranslatorManager result = new TranslatorManagerImpl(translators, errorHandler);
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.PRIMITIVE);
    }
    
    /**
     * Provides a list of sources (SymbolSource) for values of substitution symbols.
     */
    private void addSymbolSourcesConfiguration(ModuleDefinition md)
    {
        ConfigurationPointDefinition cpd = helper.addConfigurationPoint("SymbolSources", List.class.getName());

        helper.addContributionDefinition(cpd, new Contribution()
        {

            public void contribute(ContributionContext context)
            {
                List contribution = new ArrayList();
                
                // Add the default symbol sources FactoryDefaults and
                // ApplicationDefaults
                SymbolSource factoryDefaults = (SymbolSource) context.getService(
                        "FactoryDefaultsSymbolSource", SymbolSource.class);
                SymbolSourceContribution factoryDefaultsContrib = new SymbolSourceContribution(factoryDefaults,
                        "hivemind.FactoryDefaults", null, null);
                contribution.add(factoryDefaultsContrib);

                SymbolSource applicationDefaults = (SymbolSource) context.getService(
                        "ApplicationDefaultsSymbolSource", SymbolSource.class);
                SymbolSourceContribution applicationDefaultsContrib = new SymbolSourceContribution(applicationDefaults,
                        "hivemind.ApplicationDefaults", null, "hivemind.FactoryDefaults");
                contribution.add(applicationDefaultsContrib);
                
                context.mergeContribution(contribution);
            }
        });
    }
    
    /**
     * Adds a service that implements the symbol source interface and a
     * corresponding configuration point that holds the symbol values.
     */
    private void addSymbolSource(ModuleDefinition md, final String servicePointId, final String configurationId)
    {
        ServicePointDefinition spd = helper.addServicePoint(servicePointId, SymbolSource.class.getName());

        // Define inline implementation constructor, that wires the corresponding configuration
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                DefaultsSymbolSource result = new DefaultsSymbolSource();
                result.setDefaults(((Map) context.getConfiguration(configurationId)).values());
                result.initializeService();
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.SINGLETON);
        
        // Configuration point for setting defaults for symbol values.

        helper.addConfigurationPoint(configurationId, Map.class.getName());
    }  
    
    /**
     * @see SymbolExpander
     */
    private void addSymbolExpander(ModuleDefinition md, final ErrorHandler errorHandler)
    {
        ServicePointDefinition spd = helper.addServicePoint("SymbolExpander", SymbolExpander.class.getName());

        // Define inline implementation constructor, that wires ErrorHandler and SymbolSources
        ImplementationConstructor constructor = new AbstractServiceImplementationConstructor(md.getLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                List symbolSources = (List) context.getConfiguration("SymbolSources");

                SymbolExpander result = new SymbolExpanderImpl(errorHandler, symbolSources);
                return result;
            }
        };
        helper.addServiceImplementation(spd, constructor, ServiceModel.PRIMITIVE);
        
    }

}

class XmlPostProcessor implements RegistryDefinitionPostProcessor
{
    public void postprocess(RegistryDefinition registryDefinition, ErrorHandler errorHandler)
    {
        XmlExtensionResolver extensionResolver = new XmlExtensionResolver(registryDefinition, errorHandler);
        extensionResolver.resolveSchemas();
    }
}
