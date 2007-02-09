package org.apache.hivemind.definition;

import org.apache.hivemind.Locatable;

/**
 * Defines an extension to an extension point.
 * 
 * @author Huegen
 */
public interface ExtensionDefinition extends Locatable
{
    /**
     * @return the id of the module that contributed this extension
     */
    public String getModuleId();

    /**
     * @return  the module that contributed this extension
     */
    public ModuleDefinition getModule();

}