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

package org.apache.hivemind.parse;

import org.apache.hivemind.util.ToStringBuilder;

/**
 * Descriptor for &lt;implementation&gt; elements.
 * 
 * @author Howard Lewis Ship
 */
public final class ImplementationDescriptor extends AbstractServiceDescriptor
{
    private String _serviceId;

    /** @since 1.1 */

    private String _conditionalExpression;

    public String getServiceId()
    {
        return _serviceId;
    }

    public void setServiceId(String string)
    {
        _serviceId = string;
    }

    protected void extendDescription(ToStringBuilder builder)
    {
        builder.append("serviceId", _serviceId);
        builder.append("conditionalExpression", _conditionalExpression);
    }

    /** @since 1.1 */
    public String getConditionalExpression()
    {
        return _conditionalExpression;
    }

    /** @since 1.1 */
    public void setConditionalExpression(String conditionalExpression)
    {
        _conditionalExpression = conditionalExpression;
    }
}