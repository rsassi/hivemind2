package org.apache.hivemind.definition;

import org.apache.hivemind.Locatable;

/**
 * Defines an extension point of a module.
 * 
 * @author Huegen
 */
public interface ExtensionPointDefinition extends Locatable
{
    /**
     * @return the id of the module that defined this extension point
     */
    public String getModuleId();

    /**
     * @return  the id of the extension point (unqualified, without module id)
     */
    public String getId();

    /**
     * @return  the qualifed id of the extension point (includes module id)
     */
    public String getQualifiedId();

    /**
     * @return  the visibility of the extension point
     */
    public Visibility getVisibility();

}