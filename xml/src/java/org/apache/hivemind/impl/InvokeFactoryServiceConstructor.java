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
import org.apache.hivemind.ErrorLog;
import org.apache.hivemind.Location;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.Defense;
import org.apache.hivemind.util.InstanceCreationUtils;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;

/**
 * Constructs a new service by invoking methods on another service (which implements the
 * {@link org.apache.hivemind.ServiceImplementationFactory} interface.
 * 
 * @author Howard Lewis Ship
 */
public final class InvokeFactoryServiceConstructor extends AbstractServiceImplementationConstructor
{
    private String _factoryServiceId;

    /** List of {@link org.apache.hivemind.Element}, the raw XML parameters. */
    private List _parameters;

    /** The factory service to be invoked. */
    private ServiceImplementationFactory _factory;

    /** The parameters converted to objects as per the factory's parameter schema. */
    private Object _convertedParameters;
    
    public InvokeFactoryServiceConstructor(Location location, String contributingModuleId)
    {
        super(location);
    }

    public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
    {
        setupFactoryAndParameters(context.getServicePoint(), context.getDefiningModule());

        try
        {
            ServiceImplementationFactoryParameters factoryParameters = new ServiceImplementationFactoryParametersImpl(
                    context.getServicePoint(), context.getDefiningModule(), _convertedParameters);

            return _factory.createCoreServiceImplementation(factoryParameters);
        }
        catch (Exception ex)
        {
            throw new ApplicationRuntimeException(ex.getMessage(), getLocation(), ex);
        }
    }

    // A lot of changes to synchronization and service construction occured between 1.1 and 1.1.1;
    // this method was split off and made synchronized ... otherwise, it was possible for the
    // pooled or threaded services to get into a potential race condition through this code.

    private synchronized void setupFactoryAndParameters(ServicePoint servicePoint, Module contributingModule)
    {
        if (_factory == null)
        {
            ServicePoint factoryPoint = contributingModule.getServicePoint(_factoryServiceId);

            ErrorLog errorLog = servicePoint.getErrorLog();
            
            _factory = (ServiceImplementationFactory) factoryPoint
                .getService(ServiceImplementationFactory.class);

            ServicePointDefinition spd = factoryPoint.getServicePointDefinition();
            if (!(spd instanceof XmlServicePointDefinitionImpl)) {
                // TODO annotations: Externalize message
                throw new ApplicationRuntimeException("ServicePoint used as ServiceImplementationFactory must be of type XmlServicePointDefinitionImpl");
            }
            XmlServicePointDefinitionImpl xmlServicePoint = (XmlServicePointDefinitionImpl) spd;

            Schema schema = xmlServicePoint.getParametersSchema();
            if (schema != null) {
                
                Occurances expected = xmlServicePoint.getParametersCount();
                checkParameterCounts(errorLog, expected);

                SchemaProcessorImpl processor = new SchemaProcessorImpl(errorLog, schema);
                
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

    public void setFactoryServiceId(String factoryServiceId)
    {
        _factoryServiceId = factoryServiceId;
    }

    public String getFactoryServiceId()
    {
        return _factoryServiceId;
    }
    
    public Object constructParametersContainer(String containerClassName, Module definingModule)
    {
        Defense.notNull(containerClassName, "containerClassName");
        
        return InstanceCreationUtils.createInstance(
                definingModule,
                containerClassName,
                getLocation());
    }
    
    /**
     * Checks that the number of parameter elements matches the expected count.
     */
    private void checkParameterCounts(ErrorLog log, Occurances expected)
    {
        int actual = _parameters.size();

        if (expected.inRange(actual))
            return;

        String message = XmlImplMessages.wrongNumberOfParameters(_factoryServiceId, actual, expected);

        log.error(message, getLocation(), null);
    }


}