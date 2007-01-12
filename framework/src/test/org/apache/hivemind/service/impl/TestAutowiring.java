package org.apache.hivemind.service.impl;

import hivemind.test.services.ServiceAutowireTarget;
import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.Autowiring;
import org.apache.hivemind.service.AutowiringStrategy;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests related to {@link Autowiring} and {@link AutowiringStrategy}.
 * 
 * @author Achim Huegen
 */
public class TestAutowiring extends HiveMindTestCase
{
    /**
     * Ensures that all configured strategies are called in the configured order
     * until one signals successful wiring.
     */
    public void testStrategies()
    {
        // Configure an instance of AutowiringImpl with three strategies 
        MockControl strategy1Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy1 = (AutowiringStrategy) strategy1Control.getMock();
        
        // This strategy is expected to be called and wire successfully
        AutowiringStrategy strategy2 = new AutowiringByTypeStrategy();

        // This strategy is not expected to be called since the previous one is successful
        MockControl strategy3Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy3 = (AutowiringStrategy) strategy3Control.getMock();
        
        List strategyContributions = new ArrayList();
        AutowiringStrategyContribution contrib1 = new AutowiringStrategyContribution(strategy1,
                "strategy1", null, null);
        strategyContributions.add(contrib1);
        AutowiringStrategyContribution contrib2 = new AutowiringStrategyContribution(strategy2,
                "strategy2", null, null);
        strategyContributions.add(contrib2);
        AutowiringStrategyContribution contrib3 = new AutowiringStrategyContribution(strategy3,
                "strategy3", null, null);
        strategyContributions.add(contrib3);
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();
        
        ServiceAutowireTarget target = new ServiceAutowireTarget();
        
        // Training
        strategy1.autowireProperty(registry, target, "stringHolder");
        strategy1Control.setReturnValue(false);

        registry.containsService(StringHolder.class, null);
        registryControl.setReturnValue(true);

        registry.getService(StringHolder.class, null);
        registryControl.setReturnValue(new StringHolderImpl());

        replayControls();
        
        Autowiring autowiring = new AutowiringImpl(registry, strategyContributions, new DefaultErrorHandler());
        autowiring.autowireProperties(target, new String[] {"stringHolder"});

        verifyControls();

        assertNotNull(target.getStringHolder());
    }
    
    /**
     * Tests the wiring by specifying an explicit strategy 
     */
    public void testAutowireSingleStrategy()
    {
        // Configure an instance of AutowiringImpl with two strategies 
        MockControl strategy1Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy1 = (AutowiringStrategy) strategy1Control.getMock();
        
        MockControl strategy2Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy2 = (AutowiringStrategy) strategy2Control.getMock();
        
        List strategyContributions = new ArrayList();
        AutowiringStrategyContribution contrib1 = new AutowiringStrategyContribution(strategy1,
                "strategy1", null, null);
        strategyContributions.add(contrib1);
        AutowiringStrategyContribution contrib2 = new AutowiringStrategyContribution(strategy2,
                "strategy2", null, null);
        strategyContributions.add(contrib2);
        
        ServiceAutowireTarget target = new ServiceAutowireTarget();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();
        
        // Training
        strategy1.autowireProperty(registry, target, "stringHolder");
        strategy1Control.setReturnValue(false);
        
        replayControls();
        
        Autowiring autowiring = new AutowiringImpl(registry, strategyContributions, new DefaultErrorHandler());
        autowiring.autowireProperties("strategy1", target);

        verifyControls();

    }
    
    /**
     * Checks that it is not tried to wire primitives and strings
     */
    public void testSkipPrimitives()
    {
        MockControl strategy1Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy1 = (AutowiringStrategy) strategy1Control.getMock();
        
        List strategyContributions = new ArrayList();
        AutowiringStrategyContribution contrib1 = new AutowiringStrategyContribution(strategy1,
                "strategy1", null, null);
        strategyContributions.add(contrib1);

        Object target = new SkippedPropertiesAutowireTarget();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();
        
        replayControls();
        
        Autowiring autowiring = new AutowiringImpl(registry, strategyContributions, new DefaultErrorHandler());
        autowiring.autowireProperties(target);

        verifyControls();

    }
    
    /**
     * Checks that it is not tried to wire properties which are assigned already
     */
    public void testSkipNotNull()
    {
        MockControl strategy1Control = newControl(AutowiringStrategy.class);
        AutowiringStrategy strategy1 = (AutowiringStrategy) strategy1Control.getMock();
        
        List strategyContributions = new ArrayList();
        AutowiringStrategyContribution contrib1 = new AutowiringStrategyContribution(strategy1,
                "strategy1", null, null);
        strategyContributions.add(contrib1);

        ServiceAutowireTarget target = new ServiceAutowireTarget();
        target.setStringHolder(new StringHolderImpl());
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();
        
        replayControls();
        
        Autowiring autowiring = new AutowiringImpl(registry, strategyContributions, new DefaultErrorHandler());
        autowiring.autowireProperties(target);

        verifyControls();

    }

}
