package org.apache.examples.annotations.panorama;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.apache.examples.panorama.startup.impl.Task;
import org.apache.examples.panorama.startup.impl.TaskExecutor;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.annotations.AbstractAnnotatedModule;
import org.apache.hivemind.annotations.definition.Configuration;
import org.apache.hivemind.annotations.definition.Contribution;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.ErrorLogImpl;
import org.apache.hivemind.impl.MessageFormatter;

public class PanoramaStartupModule extends AbstractAnnotatedModule
{

    @Service( id="Startup" )
    public Runnable getStartupService()
    {
        TaskExecutor executor = new TaskExecutor();
        executor.setTasks(configuration("tasks", List.class));
        // Some of the logic which is automatically provided by the builder factory
        // must be done manually
        ErrorLog errorLog = new ErrorLogImpl(new DefaultErrorHandler(), LogFactory.getLog(TaskExecutor.class));
        executor.setErrorLog(errorLog);
        executor.setMessages(new MessageFormatter(PanoramaStartupModule.class, "panorama"));
        executor.setLog(LogFactory.getLog(TaskExecutor.class));
        
        return executor;
    }
    
    @Configuration( id="tasks" )
    public List<Task> getTasks()
    {
        return new ArrayList<Task>();
    }
    
    @Contribution( configurationId="hivemind.Startup" )
    public void contributeStartupServices(List services)
    {
        services.add(service("Startup", Runnable.class));
    }
    
}
