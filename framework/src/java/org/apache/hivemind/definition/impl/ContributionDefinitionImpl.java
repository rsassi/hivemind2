package org.apache.hivemind.definition.impl;

import org.apache.hivemind.Location;
import org.apache.hivemind.definition.Contribution;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.ModuleDefinition;

public class ContributionDefinitionImpl extends ExtensionDefinitionImpl implements ContributionDefinition
{
    private Contribution _contributionConstructor;
    private boolean _initial;

    public ContributionDefinitionImpl(ModuleDefinition module)
    {
        super(module);
    }

    public ContributionDefinitionImpl(ModuleDefinition module, Location location,
            Contribution contributionConstructor, boolean initial)
    {
        super(module, location);
        _contributionConstructor = contributionConstructor;
        _initial = initial;
    }

    /**
     * @see org.apache.hivemind.definition.ContributionDefinition#getContribution()
     */
    public Contribution getContribution()
    {
        return _contributionConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ContributionDefinition#setContributionConstructor(org.apache.hivemind.definition.Contribution)
     */
    public void setContributionConstructor(Contribution contributionConstructor)
    {
        _contributionConstructor = contributionConstructor;
    }

    /**
     * @see org.apache.hivemind.definition.ContributionDefinition#isInitalContribution()
     */
    public boolean isInitalContribution()
    {
        return _initial;
    }
}
