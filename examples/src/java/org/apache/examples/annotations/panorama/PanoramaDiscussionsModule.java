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
