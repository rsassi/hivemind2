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
