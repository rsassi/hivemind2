package org.apache.hivemind.definition;

import java.util.Collection;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;

public interface ModuleDefinition
{

    public ClassResolver getClassResolver();

    public Location getLocation();

    public String getId();

    /**
     * Returns the name of the package to search for class names within. By default, the package
     * name will match the module id, but this can be overridden in the module descriptor.
     */
    public String getPackageName();

    /**
     * Adds a service point definition to the module.
     * @param servicePoint  the service point
     * @throws ApplicationRuntimeException  if another service point with the same id has already been defined
     */
    public void addServicePoint(ServicePointDefinition servicePoint)
            throws ApplicationRuntimeException;

    public ServicePointDefinition getServicePoint(String id);

    public Collection getServicePoints();

    /**
     * Adds a configuration point definition to the module.
     * @param configurationPoint  the configuration point
     * @throws ApplicationRuntimeException  if another configuration point with the same id has already been defined
     */
    public void addConfigurationPoint(ConfigurationPointDefinition configurationPoint)
            throws ApplicationRuntimeException;

    public ConfigurationPointDefinition getConfigurationPoint(String id);

    public Collection getConfigurationPoints();

    public Collection getDependencies();

    public void addDependency(String dependsOnModuleId);

    public void addServiceImplementation(String qualifiedServicePointId,
            ServiceImplementationDefinition implementation);

    public void addServiceInterceptor(String qualifiedServicePointId,
            ServiceInterceptorDefinition interceptor);

    public void addContribution(String qualifiedConfigurationPointId,
            ContributionDefinition contribution);

    public void addConfigurationParser(String qualifiedConfigurationPointId,
            ConfigurationParserDefinition parser);
    
    public Collection getContributions();

    public Collection getImplementations();

    public Collection getInterceptors();
    
    public Collection getParsers();

}