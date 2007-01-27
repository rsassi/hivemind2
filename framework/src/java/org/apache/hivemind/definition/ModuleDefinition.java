package org.apache.hivemind.definition;

import java.util.Collection;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;

/**
 * Defines a module of a {@link RegistryDefinition}. 
 * A module has its own namespace in which configuration points and service
 * points are defined.
 * It can provide extension to extension points in other modules.
 * 
 * @author Huegen
 */
public interface ModuleDefinition
{
    /**
     * @return  the id of the module. It can contain dots.
     */
    public String getId();
    
    /**
     * @return  the {@link ClassResolver} used to resolve all classes referenced from 
     *          elements inside this module.
     */
    public ClassResolver getClassResolver();

    /** 
     * @return the location of the module
     */
    public Location getLocation();

    /**
     * Returns the name of the package to search for class names within. By default, the package
     * name will match the module id.
     */
    public String getPackageName();

    /**
     * Adds a service point definition to the module.
     * @param servicePoint  the service point
     * @throws ApplicationRuntimeException  if another service point with the same id has already been defined
     */
    public void addServicePoint(ServicePointDefinition servicePoint)
            throws ApplicationRuntimeException;

    /**
     * Returns a service point that is identified by its id.
     * @param id  the service point id (unqualified, without module id)
     * @return the service point definition
     */
    public ServicePointDefinition getServicePoint(String id);

    /**
     * @return  all {@link ServicePointDefinition service points} defined in this module
     */
    public Collection getServicePoints();

    /**
     * Adds a configuration point definition to the module.
     * @param configurationPoint  the configuration point
     * @throws ApplicationRuntimeException  if another configuration point with the same id has already been defined
     */
    public void addConfigurationPoint(ConfigurationPointDefinition configurationPoint)
            throws ApplicationRuntimeException;

    /**
     * Returns a configuration point that is identified by its id.
     * @param id  the configuration point id (unqualified, without module id)
     * @return the configuration point definition
     */
    public ConfigurationPointDefinition getConfigurationPoint(String id);

    /**
     * @return  all {@link ConfigurationPointDefinition configuration points} defined in this module
     */
    public Collection getConfigurationPoints();

    /**
     * Defines a dependency on another module. The presence of that module
     * is checked during registry construction.
     * 
     * @param dependsOnModuleId  the id of the module this module depends on
     */
    public void addDependency(String dependsOnModuleId);

    /**
     * @return  the ids of all modules this module depends on
     */
    public Collection getDependencies();
    
    /**
     * Adds a implementation for a service point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified service point id
     * @param implementation  the implementation definition
     */
    public void addImplementation(String qualifiedServicePointId,
            ImplementationDefinition implementation);

    /**
     * @return  all {@link ImplementationDefinition implementations} defined in this module
     *    by a call to {@link #addServiceImplementation}.
     */
    public Collection getImplementations();

    /**
     * Adds a interceptor for a service point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified service point id
     * @param interceptor  the interceptor definition
     */
    public void addInterceptor(String qualifiedServicePointId,
            InterceptorDefinition interceptor);

    /**
     * @return  all {@link InterceptorDefinition interceptors} defined in this module
     *    by a call to {@link #addServiceInterceptor}.
     */
    public Collection getInterceptors();

    /**
     * Adds a contribution for a configuration point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified configuration point id
     * @param contribution  the contribution definition
     */
    public void addContribution(String qualifiedConfigurationPointId,
            ContributionDefinition contribution);
    
    /**
     * @return  all {@link ContributionDefinition contributions} defined in this module
     *    by a call to {@link #addContribution}.
     */
    public Collection getContributions();

    /**
     * Adds a configuration parser for a configuration point which can be defined in this
     * module or another module.
     * 
     * @param qualifiedServicePointId  the fully qualified configuration point id
     * @param parser  the parser definition
     */
    public void addConfigurationParser(String qualifiedConfigurationPointId,
            ConfigurationParserDefinition parser);
    
    /**
     * @return  all {@link ConfigurationParserDefinition parsers} defined in this module
     *    by a call to {@link #addConfigurationParser}.
     */
    public Collection getConfigurationParsers();

}