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
