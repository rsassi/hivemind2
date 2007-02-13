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

package org.apache.hivemind.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.AutowiringStrategy;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Implementation of {@link AutowiringStrategy} that searches for
 * a matching service by the type of a property. 
 * 
 * @author Achim Huegen
 */
public class AutowiringByTypeStrategy implements AutowiringStrategy
{
    private static final Log LOG = LogFactory.getLog(AutowiringByTypeStrategy.class);

    public boolean autowireProperty(RegistryInfrastructure registry, Object target, String propertyName)
    {
        Class propertyType = PropertyUtils.getPropertyType(target, propertyName);

        // search for a property with an interface that matches the property type
        if (registry.containsService(propertyType, null))
        {
            Object collaboratingService = registry.getService(propertyType, null);
            PropertyUtils.write(target, propertyName, collaboratingService);

            if (LOG.isDebugEnabled())
            {
                LOG.debug("Autowired property " + propertyName + " by type to " + collaboratingService);
            }
            return true;
        } else {
            return false;
        }
    }

}
