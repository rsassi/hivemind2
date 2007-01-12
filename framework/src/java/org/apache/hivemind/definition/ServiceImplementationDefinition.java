package org.apache.hivemind.definition;

import org.apache.hivemind.definition.construction.ImplementationConstructor;

public interface ServiceImplementationDefinition extends ExtensionDefinition
{
    public String getServiceModel();

    public boolean isDefault();

    public ImplementationConstructor getServiceConstructor();

}