package org.apache.hivemind.annotations.internal;

import org.apache.hivemind.events.RegistryInitializationListener;

public interface ModuleInstanceProvider extends RegistryInitializationListener
{
    public Object getModuleInstance();
}
