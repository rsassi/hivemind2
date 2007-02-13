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

import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Submodule;

/**
 * @author Huegen
 */
@Module( id="panorama" )
public class PanoramaModule
{
    @Submodule( id="panorama.startup")
    public PanoramaStartupModule getStartupModule()
    {
        return new PanoramaStartupModule();
    }
    
    @Submodule( id="panorama.mail")
    public PanoramaMailModule getMailModule()
    {
        return new PanoramaMailModule();
    }
    
    @Submodule( id="panorama.discussions")
    public PanoramaDiscussionsModule getDiscussionsModule()
    {
        return new PanoramaDiscussionsModule();
    }

}
