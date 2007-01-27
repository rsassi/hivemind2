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

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.Location;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.events.RegistryShutdownListener;
import org.apache.hivemind.internal.ConfigurationPoint;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;

/**
 * Used to format messages used in errors and log output for classes within the impl package.
 * 
 * @author Howard Lewis Ship
 */
class ImplMessages
{
    private static final MessageFormatter _formatter = new MessageFormatter(ImplMessages.class,
            "ImplStrings");

    static String recursiveServiceBuild(ServicePoint point)
    {
        return _formatter.format("recursive-service-build", point.getExtensionPointId());
    }

    static String recursiveConfiguration(String pointId)
    {
        return _formatter.format("recursive-configuration", pointId);
    }

    static String unableToConstructConfiguration(String pointId, Throwable exception)
    {
        return _formatter.format("unable-to-construct-configuration", pointId, exception
                .getMessage());
    }

    static String unknownServiceModel(String name)
    {
        return _formatter.format("unknown-service-model", name);
    }

    static String unqualifiedServicePoint( String serviceId, String matchingIds )
    {
        return _formatter.format( "unqualified-service-point", serviceId, matchingIds );
    }
    
    static String noSuchServicePoint(String serviceId)
    {
        return _formatter.format("no-such-service-point", serviceId);
    }

    static String unableToLoadClass(String name, ClassLoader loader, Throwable cause)
    {
        return _formatter.format("unable-to-load-class", name, loader, cause);
    }

    static String nullInterceptor(InterceptorDefinition interceptor,
            ServicePoint point)
    {
        return _formatter.format("null-interceptor", interceptor.getName(), point
                .getExtensionPointId());
    }

    static String interceptorDoesNotImplementInterface(Object interceptor,
            InterceptorDefinition interceptorDefinition, ServicePoint point, Class serviceInterface)
    {
        return _formatter.format("interceptor-does-not-implement-interface", new Object[]
        { interceptor, interceptorDefinition.getName(), point.getExtensionPointId(),
                serviceInterface.getName() });
    }

    static String unableToReadMessages(URL url)
    {
        return _formatter.format("unable-to-read-messages", url);
    }

    static String unableToParse(Resource resource, Throwable cause)
    {
        return _formatter.format("unable-to-parse", resource, cause);
    }

    static String unableToFindProviders(ClassResolver resolver, Throwable cause)
    {
        return _formatter.format("unable-to-find-providers", resolver, cause);
    }
    
    static String unableToReadManifest(URL url, Throwable cause)
    {
        return _formatter.format("unable-to-read-manifest", url.toString(), cause);
    }

    static String duplicateModuleId(String moduleId, Location locationOfExisting,
            Location locationOfDuplicate)
    {
        return _formatter.format(
                "duplicate-module-id",
                moduleId,
                locationOfExisting.getResource(),
                locationOfDuplicate.getResource());
    }

    static String missingService(ServicePoint point)
    {
        return _formatter.format("missing-service", point.getExtensionPointId());
    }

    static String duplicateFactory(Module sourceModule, String pointId,
            ServicePointImpl existing)
    {
        return _formatter.format("duplicate-factory", sourceModule.getModuleId(), pointId, existing
                .getImplementationDefinition().getModuleId());
    }

    static String noSuchConfiguration(String pointId)
    {
        return _formatter.format("no-such-configuration", pointId);
    }

    static String badInterface(String interfaceName, String pointId)
    {
        return _formatter.format("bad-interface", interfaceName, pointId);
    }

    static String serviceWrongInterface(ServicePoint servicePoint, Class requestedInterface)
    {
        return _formatter.format(
                "service-wrong-interface",
                servicePoint.getExtensionPointId(),
                requestedInterface.getName(),
                servicePoint.getServiceInterface().getName());
    }

    static String shutdownCoordinatorFailure(RegistryShutdownListener listener,
            Throwable cause)
    {
        return _formatter.format("shutdown-coordinator-failure", listener, cause);
    }

    static String unlocatedError(String message)
    {
        return _formatter.format("unlocated-error", message);
    }

    static String locatedError(Location location, String message)
    {
        return _formatter.format("located-error", location, message);
    }

    static String interceptorContribution()
    {
        return _formatter.getMessage("interceptor-contribution");
    }

    static String registryAlreadyStarted()
    {
        return _formatter.getMessage("registry-already-started");
    }

    static String noServicePointForInterface(Class interfaceClass)
    {
        return _formatter.format("no-service-point-for-interface", interfaceClass.getName());
    }

    static String multipleServicePointsForInterface(Class interfaceClass,
            Collection matchingPoints)
    {
        StringBuffer buffer = new StringBuffer("{");

        boolean following = false;

        Iterator i = matchingPoints.iterator();
        while (i.hasNext())
        {
            if (following)
                buffer.append(", ");

            ServicePoint p = (ServicePoint) i.next();

            buffer.append(p.getExtensionPointId());

            following = true;
        }

        buffer.append("}");

        return _formatter.format(
                "multiple-service-points-for-interface",
                interfaceClass.getName(),
                buffer);
    }

    private static String convertModule(Module module)
    {
        if (module == null)
            return _formatter.getMessage("null-module");

        return _formatter.format("module", module.getModuleId());
    }

    static String serviceNotVisible(String serviceId, Module module)
    {
        return _formatter.format("service-not-visible", serviceId, convertModule(module));
    }

    static String configurationNotVisible(String configurationId, Module module)
    {
        return _formatter.format(
                "configuration-not-visible",
                configurationId,
                convertModule(module));
    }

    static String unableToMapConfiguration(ConfigurationPoint point)
    {
        return _formatter.format("unable-to-map-configuration", point.getExtensionPointId());
    }

    static String unableToConvertType(String type, String packageName)
    {
        return _formatter.format("unable-to-convert-type", type, packageName);
    }

    public static String unableToCreateProvider(String providerClassName, Exception cause)
    {
        return _formatter.format("unable-to-create-provider", providerClassName, cause);
    }

    public static String providerWrongType(String providerClassName, Class requiredInterface)
    {
        return _formatter.format("provider-wrong-type", providerClassName, requiredInterface.getName());
    }

    public static String servicePointDefinitionWithoutImplementation(ServicePointDefinition sd)
    {
        return _formatter.format("servicepointdefinition-without-implementation", sd.getQualifiedId());
    }
}