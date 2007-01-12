package org.apache.hivemind.annotations;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnnotatedModule extends AbstractAnnotatedModule
{
    @Service(id = "Test")
    public Runnable getRunnable()
    {
        return new Runnable()
        {

            public void run()
            {
                List<String> demoList = (List<String>) getRegistry().getConfiguration("Demo", List.class);
                for (String entry : demoList)
                {
                    System.out.println(entry);
                }
                String one = (String) getRegistry().getConfiguration("SingleElement", String.class);
                System.out.println(one);
                StringHolderImpl holder = (StringHolderImpl) getRegistry().getConfiguration("StringHolder", StringHolderImpl.class);
                System.out.println(holder.getValue());
                
                StringHolderImpl holderService = (StringHolderImpl) getRegistry().getService("StringHolder", StringHolderImpl.class);
                System.out.println(holderService.getValue());
            }
        };
    }

    @Configuration(id = "Demo")
    public List<String> getDemo()
    {
        List<String> result = new ArrayList<String>();
        result.add("initial-data");
        return result;
    }

    @Contribution(configurationId = "Demo")
    public void contributeData(List<String> container)
    {
        container.add("contributed-data");
    }

    @Contribution(configurationId = "Demo")
    public List<String> contributeData2()
    {
        List<String> data = new ArrayList<String>(); 
        data.add("contributed-data-2");
        return data;
    }
    
    @Configuration(id = "SingleElement")
    public String getSingleElementConfig()
    {
        return null;
    }

    @Contribution(configurationId = "SingleElement")
    public String contributeSingleElement()
    {
        return "The One and Only";
    }
    
    @Configuration(id = "StringHolder")
    public StringHolderImpl getStringHolder()
    {
        StringHolderImpl result = new StringHolderImpl();
        result.setValue("test");
        return result;
    }
    
    @Service(id = "StringHolder")
    public StringHolderImpl getStringHolderService()
    {
        StringHolderImpl result = new StringHolderImpl();
        result.setValue("test");
        return result;
    }

//    @Contribution(configurationId = "hivemind.ApplicationDefaults")
//    public void contributeDefaults(HashMap<String,FactoryDefault> container)
//    {
//        container.put("testsymbol", new FactoryDefault("testsymbol", "value"));
//    }
    
}
