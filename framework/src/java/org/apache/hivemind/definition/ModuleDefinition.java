package org.apache.hivemind.definition;

import java.util.Collection;

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
     * @return  the ids of all modules this module depends on
     */
    public Collection getDependencies();
    
    /**
     * @return  all {@link ImplementationDefinition implementations} defined in this module
     *    by a call to {@link #addServiceImplementation}.
     */
    public Collection getImplementations();

    /**
     * @return  all {@link InterceptorDefinition interceptors} defined in this module
     *    by a call to {@link #addServiceInterceptor}.
     */
    public Collection getInterceptors();

    /**
     * @return  all {@link ContributionDefinition contributions} defined in this module
     *    by a call to {@link #addContribution}.
     */
    public Collection getContributions();

    /**
     * @return  all {@link ConfigurationParserDefinition parsers} defined in this module
     *    by a call to {@link #addConfigurationParser}.
     */
    public Collection getConfigurationParsers();

}