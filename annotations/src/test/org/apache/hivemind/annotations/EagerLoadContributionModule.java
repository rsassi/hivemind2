package org.apache.hivemind.annotations;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.Collections;
import java.util.List;

import org.apache.hivemind.annotations.definition.Contribution;
import org.apache.hivemind.annotations.definition.Service;

/**
 * Module that eager loads a service by contributing it to "hivemind.EagerLoad"
 */
public class EagerLoadContributionModule extends AbstractAnnotatedModule
{
    @Service(id = "StringHolder")
    public StringHolder getStringHolderService()
    {
        StringHolderImpl result = new StringHolderImpl();
        result.setValue("test");
        return result;
    }
   
//    @Contribution(configurationId="hivemind.EagerLoad")
//    public List eagerLoad() {
//        return Collections.singletonList(service(StringHolder.class ));
//    }

}
