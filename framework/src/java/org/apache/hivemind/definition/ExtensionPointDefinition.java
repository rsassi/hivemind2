package org.apache.hivemind.definition;

import org.apache.hivemind.Locatable;

public interface ExtensionPointDefinition extends Locatable
{
    /**
     * @return the id of the module that defined this extension point
     */
    public String getModuleId();

    public String getFullyQualifiedId();

    public String getId();

    public Visibility getVisibility();

}