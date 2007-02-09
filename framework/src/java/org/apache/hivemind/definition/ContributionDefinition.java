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