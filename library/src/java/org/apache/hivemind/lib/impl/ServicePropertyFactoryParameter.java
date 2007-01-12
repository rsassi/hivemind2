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

package org.apache.hivemind.lib.impl;

import org.apache.hivemind.impl.BaseLocatable;
import org.apache.hivemind.internal.ServicePoint;

/**
 * Parameter object used with {@link ServicePropertyFactory}.
 *
 * @author Howard Lewis Ship
 */
public class ServicePropertyFactoryParameter extends BaseLocatable
{
    private ServicePoint _servicePoint;
    private String _propertyName;

    public String getPropertyName()
    {
        return _propertyName;
    }

    public ServicePoint getServicePoint()
    {
        return _servicePoint;
    }

    public void setPropertyName(String string)
    {
        _propertyName = string;
    }

    public void setServicePoint(ServicePoint servicePoint)
    {
        _servicePoint = servicePoint;
    }

}
