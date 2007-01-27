package org.apache.hivemind.definition;

import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.internal.ServiceModel;

/**
 * Helper class that offers convenience functions for the definition of modules
 * and its extension points. All instances created are 
 * {@link org.apache.hivemind.definition.impl standard implementations} 
 * of the definition interfaces.
 * 
 * @author Achim Huegen
 */
public class ModuleDefinitionHelper
{
    private ModuleDefinition _module;

    public ModuleDefinitionHelper(ModuleDefinition module)
    {
        _module = module;
    }

    public ServicePointDefinition addServicePoint(String servicePointId, String serviceInterface)
    {
        ServicePointDefinitionImpl result = new ServicePointDefinitionImpl(_module, servicePointId, _module
                .getLocation(), Visibility.PUBLIC, serviceInterface);

        _module.addServicePoint(result);
        return result;
    }

    public ServicePointDefinition addServicePointWithDefaultImplementation(String servicePointId, String serviceInterface)
    {
        ServicePointDefinition result = addServicePoint(servicePointId, serviceInterface);
        String defaultImplementationName = serviceInterface + "Impl";
        addSimpleServiceImplementation(result, defaultImplementationName, ServiceModel.SINGLETON);
        return result;
    }
 
    public ImplementationDefinition addServiceImplementation(
            ServicePointDefinition servicePoint,
            ImplementationConstructor constructor, String serviceModel
            )
    {
        // These implementations override the inline implementations, so default is true here
        ImplementationDefinition result = new ServiceImplementationDefinitionImpl(_module, _module
                .getLocation(), constructor, serviceModel, true);
        servicePoint.addImplementation(result);
        return result;
    }
    
    public ImplementationDefinition addSimpleServiceImplementation(
            ServicePointDefinition servicePoint,
            String serviceImplementationClass, String serviceModel)
    {
        return addServiceImplementation(servicePoint, 
                new CreateClassServiceConstructor(_module.getLocation(), serviceImplementationClass), 
                serviceModel);
    }
    
    public ConfigurationPointDefinition addConfigurationPoint(String configurationPointId, String containerType)
    {
        ConfigurationPointDefinitionImpl result = new ConfigurationPointDefinitionImpl(_module, configurationPointId, _module
                .getLocation(), Visibility.PUBLIC, containerType, Occurances.UNBOUNDED);

        _module.addConfigurationPoint(result);
        return result;
    }
    
    public ContributionDefinition addContributionDefinition(ConfigurationPointDefinition configurationPoint,
            Contribution contributionConstructor)
    {
        ContributionDefinition result = new ContributionDefinitionImpl(_module, _module.getLocation(), contributionConstructor, false);
        configurationPoint.addContribution(result);
        return result;
    }
    
    public ContributionDefinition addContributionDefinition(String fullyQualifiedConfigurationPointId, 
            Contribution contributionConstructor)
    {
        ContributionDefinitionImpl result = new ContributionDefinitionImpl(_module, _module.getLocation(), contributionConstructor, false);
        _module.addContribution(fullyQualifiedConfigurationPointId, result);
        return result;
    }

    public ModuleDefinition getModule()
    {
        return _module;
    }

}
