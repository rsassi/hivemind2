// Copyright 2007 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
