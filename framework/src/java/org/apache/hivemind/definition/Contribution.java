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

package org.apache.hivemind.definition;

/**
 * Contributes data to a {@link org.apache.hivemind.definition.ConfigurationPointDefinition configuration point}.
 * 
 * @author Achim Huegen
 */
public interface Contribution
{
    /**
     * Contributes to the configuration. The context provides methods to manipulate the
     * configuration data, that is to merge with or override data already provided 
     * by other contributions
     * 
     * @param context  provides access to elements of the registry which are needed for the contribution.
     */
    public void contribute(ContributionContext context);
}