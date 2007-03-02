package org.apache.hivemind.annotations.definition.impl;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;

public class AnnotatedModuleDefinitionImpl extends ModuleDefinitionImpl
{

    public AnnotatedModuleDefinitionImpl(String id, Location location, ClassResolver resolver, String packageName)
    {
        super(id, location, resolver, packageName);
    }

    public AnnotatedModuleDefinitionImpl(String id, Location location)
    {
        super(id, location);
    }

}
