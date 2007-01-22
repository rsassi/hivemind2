package org.apache.hivemind.definition;


public interface ImplementationDefinition extends ExtensionDefinition
{
    public String getServiceModel();

    public boolean isDefault();

    public ImplementationConstructor getServiceConstructor();

}