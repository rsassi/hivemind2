package org.apache.examples.annotations.panorama;

import java.util.List;

import org.apache.examples.panorama.discussions.DiscussionsStartup;
import org.apache.examples.panorama.startup.impl.ExecuteStatic;
import org.apache.examples.panorama.startup.impl.Task;
import org.apache.hivemind.annotations.AbstractAnnotatedModule;
import org.apache.hivemind.annotations.definition.Contribution;

public class PanoramaDiscussionsModule extends AbstractAnnotatedModule
{
    @Contribution( configurationId="panorama.startup.tasks" )
    public void contributeTaks(List<Task> tasks)
    {
        Task discussionStartupTask = new Task();
        ExecuteStatic executable = new ExecuteStatic();
        executable.setTargetClass(DiscussionsStartup.class);
        discussionStartupTask.setExecutable(executable);
        discussionStartupTask.setId("discussions");
        discussionStartupTask.setTitle("Discussions");
        
        tasks.add(discussionStartupTask);
    }
}
