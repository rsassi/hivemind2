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

package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

/**
 * Default implementation of {@link ContributionDefinition}.
 * 
 * @author Achim Huegen
 */
public class ContributionDefinitionImpl extends ExtensionDefinitionImpl implements ContributionDefinition
{
    private Contribution _contribution;
    private boolean _initial;

    public ContributionDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ContributionDefinitionImpl(ModuleDefinition module, Location location,
            Contribution contribution, boolean initial)
    {
        super(module, location);
        _contribution = contribution;
        _initial = initial;
    }

    /**
     * @see org.apache.hivemind.definition.ContributionDefinition#getContribution()
     */
    public Contribution getContribution()
    {
        return _contribution;
    }

    /**
     * Sets the contribution implementation.
     */
    public void setContribution(Contribution contribution)
    {
        _contribution = contribution;
    }

    /**
     * @see org.apache.hivemind.definition.ContributionDefinition#isInitalContribution()
     */
    public boolean isInitalContribution()
    {
        return _initial;
    }
}
