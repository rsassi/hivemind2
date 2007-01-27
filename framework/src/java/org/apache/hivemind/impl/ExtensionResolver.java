package org.apache.hivemind.impl;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.UnresolvedExtension;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.util.IdUtils;

/**
 * Resolves the {@link UnresolvedExtension unresolved extensions} in all
 * modules of a {@link RegistryDefinition} during
 * the construction of a registry by {@link RegistryBuilder}.
 * Every unresolved extension (e.g. contribution, interceptor) references
 * an extension point by its fully qualified id. This class looks for these
 * extension points in the registry definition and adds the formerly unresolved
 * extension directly to the extension points.
 * The error handling is delegated to an instance of {@link ErrorHandler}.
 * 
 * @author Achim Huegen
 */
public class ExtensionResolver
{
    private static final Log LOG = LogFactory.getLog(ExtensionResolver.class);

    private ErrorHandler _errorHandler;
    
    private RegistryDefinition _definition;

    public ExtensionResolver(RegistryDefinition definition, ErrorHandler errorHandler)
    {
        _errorHandler = errorHandler;
        _definition = definition;
    }

    /**
     * Resolves all unresolved extensions in the registry definition passed in the constructor.
     * During this process the object graph represented by the registry definition is changed, 
     * so that afterwards it doesn't contain unresolved extensions any longer if anything went ok. 
     * If errors occur it depends on the assigned {@link ErrorHandler} whether an exceptions
     * is raised or errors are logged only. In the latter case all extensions that couldn't
     * be resolved will remain in the module definitions.
     */
    public void resolveExtensions()
    {
        for (Iterator iterModules = _definition.getModules().iterator(); iterModules.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) iterModules.next();
        
            resolveImplementations(module);
            resolveInterceptors(module);
            resolveContributions(module);
            resolveConfigurationParsers(module);
        }
    }

    private void resolveImplementations(ModuleDefinition module)
    {
        for (Iterator iter = module.getImplementations().iterator(); iter.hasNext();)
        {
            UnresolvedExtension unresolved = (UnresolvedExtension) iter.next();
            String servicePointId = unresolved.getExtensionPointId();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to resolve service point " + servicePointId + " referenced by" +
                        " implementation" + logLocation(unresolved.getExtension().getLocation()));
            }
            ServicePointDefinition servicePoint = _definition.getServicePoint(servicePointId);
            if (servicePoint == null)
            {
                _errorHandler.error(
                        LOG,
                        DefinitionMessages.unknownServicePoint(
                                IdUtils.extractModule(servicePointId),
                                IdUtils.stripModule(servicePointId)),
                        unresolved.getExtension().getLocation(),
                        null);
            } else {
                servicePoint.addImplementation((ImplementationDefinition) unresolved.getExtension());
            }
            iter.remove();
        }
    }
    
    private String logLocation(Location location)
    {
        if (location == null) {
            return "";
        } else {
            return " at " + location.toString();
        }
    }
    
    private void resolveInterceptors(ModuleDefinition module)
    {
        for (Iterator iter = module.getInterceptors().iterator(); iter.hasNext();)
        {
            UnresolvedExtension unresolved = (UnresolvedExtension) iter.next();
            String servicePointId = unresolved.getExtensionPointId();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to resolve service point " + servicePointId + " referenced by" +
                        " interceptor" + logLocation(unresolved.getExtension().getLocation()));
            }
            ServicePointDefinition servicePoint = _definition.getServicePoint(servicePointId);
            if (servicePoint == null)
            {
                _errorHandler.error(
                        LOG,
                        DefinitionMessages.unknownServicePoint(
                                IdUtils.extractModule(servicePointId),
                                IdUtils.stripModule(servicePointId)),
                        unresolved.getExtension().getLocation(),
                        null);
            } else {
                servicePoint.addInterceptor((InterceptorDefinition) unresolved.getExtension());
            }
            iter.remove();
        }
    }
    
    private void resolveContributions(ModuleDefinition module)
    {
        for (Iterator iter = module.getContributions().iterator(); iter.hasNext();)
        {
            UnresolvedExtension unresolved = (UnresolvedExtension) iter.next();
            String configurationPointId = unresolved.getExtensionPointId();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to resolve configuration point " + configurationPointId + " referenced by" +
                        " contribution " + logLocation(unresolved.getExtension().getLocation()));
            }
            ConfigurationPointDefinition configurationPoint = _definition.getConfigurationPoint(configurationPointId);
            if (configurationPoint == null)
            {
                _errorHandler.error(
                        LOG,
                        DefinitionMessages.unknownConfigurationPoint(
                                IdUtils.extractModule(configurationPointId),
                                IdUtils.stripModule(configurationPointId)),
                        unresolved.getExtension().getLocation(),
                        null);
            } else {
                configurationPoint.addContribution((ContributionDefinition) unresolved.getExtension());
            }
            iter.remove();
        }
    }
    
    private void resolveConfigurationParsers(ModuleDefinition module)
    {
        for (Iterator iter = module.getConfigurationParsers().iterator(); iter.hasNext();)
        {
            UnresolvedExtension unresolved = (UnresolvedExtension) iter.next();
            String configurationPointId = unresolved.getExtensionPointId();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Trying to resolve configuration point " + configurationPointId + " referenced by" +
                        " ConfigurationParser " + logLocation(unresolved.getExtension().getLocation()));
            }
            ConfigurationPointDefinition configurationPoint = _definition.getConfigurationPoint(configurationPointId);
            if (configurationPoint == null)
            {
                _errorHandler.error(
                        LOG,
                        DefinitionMessages.unknownConfigurationPoint(
                                IdUtils.extractModule(configurationPointId),
                                IdUtils.stripModule(configurationPointId)),
                        unresolved.getExtension().getLocation(),
                        null);
            } else {
                if (Visibility.PRIVATE.equals(configurationPoint.getVisibility())
                   && !module.getId().equals(IdUtils.extractModule(configurationPointId))) {
                    _errorHandler.error(
                            LOG,
                            DefinitionMessages.configurationPointNotVisible(
                                    configurationPoint,
                                    module),
                            unresolved.getExtension().getLocation(),
                            null);
                    
                }
                
                configurationPoint.addParser((ConfigurationParserDefinition) unresolved.getExtension());
            }
            iter.remove();
        }
    }

}
