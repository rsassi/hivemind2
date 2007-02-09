package org.apache.hivemind.annotations;

@Module(id = "super")
public class Supermodule extends AbstractAnnotatedModule
{
    @Submodule(id = "sub")
    public Submodule1 getSubmodule()
    {
        return null;
    }

}
