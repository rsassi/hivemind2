package org.apache.hivemind.definition.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.DefinitionMessages;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.UnresolvedExtension;

public class ModuleDefinitionImpl implements ModuleDefinition
{
    private String _id;

    private Location _location;

    private String _packageName;

    private ClassResolver _classResolver;
    
    private Map _servicePoints = new HashMap();
    
    private Map _configurationPoints = new HashMap();
    
    private Collection _dependencies = new ArrayList();

    private Collection _unresolvedImplementations = new ArrayList();

    private Collection _unresolvedContributions = new ArrayList();

    private Collection _unresolvedInterceptors = new ArrayList();
    
    private Collection _unresolvedConfigurationParsers = new ArrayList();
    
    public ModuleDefinitionImpl()
    {
    }

    /**
     * @param id
     * @param location
     * @param resolver
     * @param packageName  name of the package to search for class names within. If null, it defaults to the id 
     */
    public ModuleDefinitionImpl(String id, Location location, ClassResolver resolver, String packageName)
    {
        _id = id;
        _location = location;
        _classResolver = resolver;
        if (packageName != null)
            _packageName = packageName;
        else _packageName = id;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getClassResolver()
     */
    public ClassResolver getClassResolver()
    {
        return _classResolver;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setClassResolver(org.apache.hivemind.ClassResolver)
     */
    public void setClassResolver(ClassResolver classResolver)
    {
        _classResolver = classResolver;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getLocation()
     */
    public Location getLocation()
    {
        return _location;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setLocation(org.apache.hivemind.Location)
     */
    public void setLocation(Location location)
    {
        _location = location;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getId()
     */
    public String getId()
    {
        return _id;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setId(java.lang.String)
     */
    public void setId(String moduleId)
    {
        this._id = moduleId;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getPackageName()
     */
    public String getPackageName()
    {
        return _packageName;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#setPackageName(java.lang.String)
     */
    public void setPackageName(String packageName)
    {
        _packageName = packageName;
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addServicePoint(org.apache.hivemind.definition.ServicePointDefinition)
     */
    public void addServicePoint(ServicePointDefinition servicePoint)
    {
        if (_servicePoints.containsKey(servicePoint.getId())) {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateServicePointId(servicePoint.getFullyQualifiedId(), 
                    this));
        }
        _servicePoints.put(servicePoint.getId(), servicePoint);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getServicePoint(java.lang.String)
     */
    public ServicePointDefinition getServicePoint(String id)
    {
        return (ServicePointDefinition) _servicePoints.get(id);
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getServicePoints()
     */
    public Collection getServicePoints()
    {
        return _servicePoints.values();
    }
 
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addConfigurationPoint(org.apache.hivemind.definition.ConfigurationPointDefinition)
     */
    public void addConfigurationPoint(ConfigurationPointDefinition configurationPoint)
    {
        if (_configurationPoints.containsKey(configurationPoint.getId())) {
            throw new ApplicationRuntimeException(DefinitionMessages.duplicateConfigurationPointId(configurationPoint.getId(), 
                    this));
        }
        _configurationPoints.put(configurationPoint.getId(), configurationPoint);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getConfigurationPoint(java.lang.String)
     */
    public ConfigurationPointDefinition getConfigurationPoint(String id)
    {
        return (ConfigurationPointDefinition) _configurationPoints.get(id);
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getConfigurationPoints()
     */
    public Collection getConfigurationPoints()
    {
        return _configurationPoints.values();
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getDependencies()
     */
    public Collection getDependencies()
    {
        return _dependencies;
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addDependency(java.lang.String)
     */
    public void addDependency(String dependsOnModuleId) 
    {
        _dependencies.add(dependsOnModuleId);
    }
   
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addServiceImplementation(java.lang.String, org.apache.hivemind.definition.ImplementationDefinition)
     */
    public void addServiceImplementation(String qualifiedServicePointId,
            ImplementationDefinition implementation)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(implementation,
                qualifiedServicePointId);
        _unresolvedImplementations.add(unresolvedExtension);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addServiceInterceptor(java.lang.String, org.apache.hivemind.definition.InterceptorDefinition)
     */
    public void addServiceInterceptor(String qualifiedServicePointId,
            InterceptorDefinition interceptor)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(interceptor,
                qualifiedServicePointId);
        _unresolvedInterceptors.add(unresolvedExtension);
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addContribution(java.lang.String, org.apache.hivemind.definition.ContributionDefinition)
     */
    public void addContribution(String qualifiedConfigurationPointId,
            ContributionDefinition contribution)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(contribution,
                qualifiedConfigurationPointId);
        _unresolvedContributions.add(unresolvedExtension);
    }
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#addConfigurationParser(java.lang.String, org.apache.hivemind.definition.ConfigurationParserDefinition)
     */
    public void addConfigurationParser(String qualifiedConfigurationPointId, ConfigurationParserDefinition parser)
    {
        UnresolvedExtension unresolvedExtension = new UnresolvedExtension(parser,
                qualifiedConfigurationPointId);
        _unresolvedConfigurationParsers.add(unresolvedExtension);
    }  
    
    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getContributions()
     */
    public Collection getContributions()
    {
        return _unresolvedContributions;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getImplementations()
     */
    public Collection getImplementations()
    {
        return _unresolvedImplementations;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getInterceptors()
     */
    public Collection getInterceptors()
    {
        return _unresolvedInterceptors;
    }

    /**
     * @see org.apache.hivemind.definition.ModuleDefinition#getParsers()
     */
    public Collection getParsers()
    {
        return _unresolvedConfigurationParsers;
    }

  
}
