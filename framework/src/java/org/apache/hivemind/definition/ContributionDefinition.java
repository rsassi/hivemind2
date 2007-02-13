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

/**
 * Defines a contribution to a {@link ConfigurationPointDefinition}.
 * The actual contribution is done by an instance of {@link Contribution}.
 * 
 * @author Huegen
 */
public interface ContributionDefinition extends ExtensionDefinition
{
    /**
     * @return  the contribution implementation.
     */
    public Contribution getContribution();

    /**
     * @return  true if the contribution is the initial one to be processed first. Only one
     *          contribution of a configuration point is allowed to return true here. 
     */
    public boolean isInitalContribution();
}