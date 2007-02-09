package org.apache.hivemind.annotations;

import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Service;

import hivemind.test.services.impl.StringHolderImpl;

@Module(id = "theSubModule")
public class Submodule1 extends AbstractAnnotatedModule
{
    @Service(id = "StringHolder")
    public StringHolderImpl getStringHolderService()
    {
        StringHolderImpl result = new StringHolderImpl();
        result.setValue("test");
        return result;
    }

}
