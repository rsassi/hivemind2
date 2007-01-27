package org.apache.hivemind.definition;

/**
 * Represents an unresolved extension of an extension point in a registry definition. 
 * For example a contribution to a configuration point. 
 * An extension is regarded is unresolved if the corresponding definition is not directly
 * added as object to the extension point definition but is associated by the fully
 * qualified extension point id only. 
 * 
 * @author Achim Huegen
 */
public class UnresolvedExtension
{
    private String _extensionPointId;
    private ExtensionDefinition _extension;
    
    public UnresolvedExtension(ExtensionDefinition extension, String qualifiedExtensionPointId)
    {
        _extension = extension;
        _extensionPointId = qualifiedExtensionPointId;
    }

    /**
     * @return  qualified id of the extension point which is referenced by the extension 
     */
    public String getExtensionPointId()
    {
        return _extensionPointId;
    }

    /**
     * @return  the extension
     */
    public ExtensionDefinition getExtension()
    {
        return _extension;
    }

}
