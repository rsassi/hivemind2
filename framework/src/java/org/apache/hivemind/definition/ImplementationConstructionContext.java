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

package org.apache.hivemind.definition;

import org.apache.hivemind.internal.ServicePoint;

/**
 * Provides access to information and services needed
 * during the construction of an {@link ImplementationConstructor implementation}.
 * 
 * @author Huegen
 */
public interface ImplementationConstructionContext extends ConstructionContext
{
    /**
     * @return  the service point that is currently constructed
     */
    public ServicePoint getServicePoint();

}
