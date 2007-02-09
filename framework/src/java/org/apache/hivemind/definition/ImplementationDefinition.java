package org.apache.hivemind.definition;

import org.apache.hivemind.internal.ServiceModel;

/**
 * Defines an implementation of a {@link ServicePointDefinition service point}. 
 * The implementation instance is created by a {@link ImplementationConstructor}.
 * 
 * @author Huegen
 */
public interface ImplementationDefinition extends ExtensionDefinition
{
    /**
     * @return the name of the {@link ServiceModel} to use 
     */
    public String getServiceModel();

    /**
     * @return  the constructor for the creation of the implementation instance.
     */
    public ImplementationConstructor getServiceConstructor();

    /**
     * @return  true, if this is the default implementation of the service
     */
    public boolean isDefault();

}