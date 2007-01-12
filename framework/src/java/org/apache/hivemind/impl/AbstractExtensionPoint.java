// Copyright 2004, 2005 The Apache Software Foundation
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

package org.apache.hivemind.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ExtensionPointDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.internal.ExtensionPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.util.ToStringBuilder;

/**
 * Base class for extension points; provides module, visibility and extensionPointId properties.
 * 
 * @author Howard Lewis Ship
 */
/**
 * @author Huegen
 */
public abstract class AbstractExtensionPoint extends BaseLocatable implements ExtensionPoint
{
    private ExtensionPointDefinition _definition;

    private Module _module;

    /** @since 1.1 */

    private ErrorLog _errorLog;
    
    /**
     * @param definition  the definition of this extension point
     */
    public AbstractExtensionPoint(Module module, ExtensionPointDefinition definition)
    {
        _module = module;
        _definition = definition;
    }
    
    public synchronized String toString()
    {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("extensionPointId", getExtensionPointId());
        builder.append("visibility", getVisibility());

        extendDescription(builder);

        return builder.toString();
    }

    /**
     * Implemented in subclasses to provide details about subclass properties.
     */
    protected abstract void extendDescription(ToStringBuilder builder);

    public Visibility getVisibility()
    {
        return _definition.getVisibility();
    }
    
    public Location getLocation()
    {
        return _definition.getLocation();
    }
    
    public String getExtensionPointId()
    {
        return _module.getModuleId() + "." + _definition.getId();
    }

    public Module getModule()
    {
        return _module;
    }

    /**
     * Returns true if the extension point is public, or the extgension point is visible to the
     * module.
     * 
     * @param module
     *            The module to validate visibility against, or null for no module ... such as when
     *            the application accesses an extension via {@link org.apache.hivemind.Registry}.
     * @since 1.1
     */
    public boolean visibleToModule(Module module)
    {
        if (getVisibility() == Visibility.PUBLIC)
            return true;

        return _module.equals(module);
    }

    /** @since 1.1 */
    public Log getLog()
    {
        return LogFactory.getLog(getExtensionPointId());
    }

    /** @since 1.1 */
    public synchronized ErrorLog getErrorLog()
    {
        if (_errorLog == null)
            _errorLog = new ErrorLogImpl(_module.getErrorHandler(), getLog());

        return _errorLog;
    }

    protected ExtensionPointDefinition getDefinition()
    {
        return _definition;
    }   
    

}