package org.apache.hivemind.annotations;

import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Submodule;

@Module(id = "super")
public class Supermodule extends AbstractAnnotatedModule
{
    @Submodule(id = "sub")
    public Submodule1 getSubmodule()
    {
        return null;
    }

}
