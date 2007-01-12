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

import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.Location;
import org.apache.hivemind.Orderable;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.internal.AbstractServiceInterceptorConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.InstanceCreationUtils;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;

/**
 * Constructs a new interceptor by invoking methods on another service (which implements the
 * {@link org.apache.hivemind.ServiceInterceptorFactory} interface.
 */
public final class InvokeFactoryInterceptorConstructor extends AbstractServiceInterceptorConstructor implements
        Orderable
{
    private String _factoryServiceId;
    
    private ServiceInterceptorFactory _factory;

    /** List of {@link org.apache.hivemind.Element}, the raw XML parameters. */
    private List _parameters;

    /** The parameters converted to objects as per the factory's parameter schema. */
    private Object _convertedParameters;

    private String _precedingInterceptorIds;

    private String _followingInterceptorIds;
    
    public InvokeFactoryInterceptorConstructor(Location location)
    {
        super(location);
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return _factoryServiceId;
    }
    
    public void constructServiceInterceptor(InterceptorStack stack, Module contributingModule)
    {
        setup(contributingModule);

        _factory.createInterceptor(stack, contributingModule, _convertedParameters);
    }

    // A lot of changes to synchronization and service construction occured between 1.1 and 1.1.1;
    // this method was split off and made synchronized ... otherwise, it was possible for the
    // pooled or threaded services to get into a potential race condition through this code.

    private synchronized void setup(Module contributingModule)
    {
        if (_factory == null)
        {
            ServicePoint factoryPoint = contributingModule.getServicePoint(_factoryServiceId);

            _factory = (ServiceInterceptorFactory) factoryPoint
                    .getService(ServiceInterceptorFactory.class);

            ServicePointDefinition spd = factoryPoint.getServicePointDefinition();
            if (!(spd instanceof XmlServicePointDefinitionImpl)) {
                // TODO annotations: Externalize message
                throw new ApplicationRuntimeException("ServicePoint used as InterceptorFactory must be of type XmlServicePointDefinitionImpl");
            }
            XmlServicePointDefinitionImpl xmlServicePoint = (XmlServicePointDefinitionImpl) spd;
            Schema schema = xmlServicePoint.getParametersSchema();
            if (schema != null) {

                SchemaProcessorImpl processor = new SchemaProcessorImpl(factoryPoint.getErrorLog(),
                        schema);
                
                _convertedParameters = constructParametersContainer(schema.getRootElementClassName(), factoryPoint.getModule());
                processor.process(_convertedParameters, _parameters, contributingModule);
            }
        }
    }

    public List getParameters()
    {
        return _parameters;
    }

    public void setParameters(List list)
    {
        _parameters = list;
    }

    public void setFactoryServiceId(String string)
    {
        _factoryServiceId = string;
    }
    
    public Object constructParametersContainer(String containerClassName, Module definingModule)
    {
        Defense.notNull(containerClassName, "containerClassName");
        
        return InstanceCreationUtils.createInstance(
                definingModule,
                containerClassName,
                getLocation());
    }

    public void setFollowingInterceptorIds(String ids)
    {
        _followingInterceptorIds = ids;
    }

    public void setPrecedingInterceptorIds(String ids)
    {
        _precedingInterceptorIds = ids;
    }

    public String getFollowingNames()
    {
        return _followingInterceptorIds;
    }

    public String getPrecedingNames()
    {
        return _precedingInterceptorIds;
    }
}