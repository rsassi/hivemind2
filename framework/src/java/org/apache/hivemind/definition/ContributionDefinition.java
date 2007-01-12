package org.apache.hivemind.definition;

import org.apache.hivemind.definition.construction.Contribution;

public interface ContributionDefinition extends ExtensionDefinition
{
    public Contribution getContribution();

    /**
     * @return  true if the contribution is the initial one to be processed first. Only one
     *          contribution of a configuration point is allowed to return true here. 
     */
    public boolean isInitalContribution();
}