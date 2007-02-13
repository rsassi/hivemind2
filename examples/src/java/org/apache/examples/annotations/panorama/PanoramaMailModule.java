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

import java.util.List;

import org.apache.examples.panorama.mail.MailStartup;
import org.apache.examples.panorama.startup.Executable;
import org.apache.examples.panorama.startup.impl.Task;
import org.apache.hivemind.annotations.AbstractAnnotatedModule;
import org.apache.hivemind.annotations.definition.Contribution;
import org.apache.hivemind.annotations.definition.Service;

public class PanoramaMailModule extends AbstractAnnotatedModule
{
    @Service( id="MailStartup" )
    public Executable getMailStartupService()
    {
        Executable startup = new MailStartup();
        return startup;
    }
    
    @Contribution( configurationId="panorama.startup.tasks" )
    public void contributeTaks(List<Task> tasks)
    {
        Task mailStartupTask = new Task();
        mailStartupTask.setExecutable(service("MailStartup", Executable.class));
        mailStartupTask.setId("mail");
        mailStartupTask.setTitle("Mail");
        
        tasks.add(mailStartupTask);
    }

}
