package org.apache.examples.definition;

import org.apache.examples.Adder;
import org.apache.examples.Calculator;
import org.apache.examples.impl.AdderImpl;
import org.apache.examples.impl.CalculatorImpl;
import org.apache.hivemind.Location;
import org.apache.hivemind.Registry;
import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.ImplementationConstructionContext;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.definition.impl.ImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ModuleDefinitionImpl;
import org.apache.hivemind.definition.impl.RegistryDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.impl.DefaultClassResolver;
import org.apache.hivemind.impl.LocationImpl;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.internal.AbstractServiceImplementationConstructor;
import org.apache.hivemind.internal.ServiceModel;
import org.apache.hivemind.util.ClasspathResource;

/**
 * Demonstrates the use of the java registry definition API.
 * 
 * @author Achim Huegen
 */
public class JavaDefinitionExample
{
    public static void main(String[] args)
    {
        JavaDefinitionExample example = new JavaDefinitionExample();
        Registry registry = example.constructRegistry();
        
        Calculator calculator = (Calculator) registry.getService(Calculator.class);
        double result = calculator.add(10, 20);
        System.out.println("Result: " + result);
    }
    
    private Registry constructRegistry()
    {
        RegistryDefinition registryDefinition = new RegistryDefinitionImpl();
        ModuleDefinition module = defineModule(registryDefinition);
        registryDefinition.addModule(module);
        
        RegistryBuilder builder = new RegistryBuilder(registryDefinition);
        Registry registry = builder.constructRegistry();
        return registry;
    }

    private ModuleDefinition defineModule(RegistryDefinition registryDefinition)
    {
        ModuleDefinitionImpl module = new ModuleDefinitionImpl("calculator", createLocation());
         
        // Define the calculator service
        ServicePointDefinitionImpl calculatorService = new ServicePointDefinitionImpl(module, 
                "Calculator", null, Visibility.PUBLIC, Calculator.class.getName());

        // Define inline implementation constructor, that wires the Adder service
        ImplementationConstructor calculatorConstructor = new AbstractServiceImplementationConstructor(createLocation())
        {
            public Object constructCoreServiceImplementation(ImplementationConstructionContext context)
            {
                CalculatorImpl result = new CalculatorImpl();
                result.setAdder((Adder) context.getService(Adder.class));
                return result;
            }
        };
        ImplementationDefinition calculatorImplementation = new ImplementationDefinitionImpl(module, null, 
                calculatorConstructor, ServiceModel.PRIMITIVE, true);
        calculatorService.addImplementation(calculatorImplementation);
        module.addServicePoint(calculatorService);
        
        // Define the adder service
        ServicePointDefinitionImpl adderService = new ServicePointDefinitionImpl(module, 
                "Adder", null, Visibility.PUBLIC, Adder.class.getName());

        ImplementationDefinition adderImplementation = new ImplementationDefinitionImpl(module, null, 
                new CreateClassServiceConstructor(createLocation(), AdderImpl.class.getName()), 
                ServiceModel.PRIMITIVE, true);
        adderService.addImplementation(adderImplementation);
        module.addServicePoint(adderService);
        
        return module;
    }
    
    protected static Location createLocation()
    {
        String path = "/" + JavaDefinitionExample.class.getName().replace('.', '/');

        Resource r = new ClasspathResource(new DefaultClassResolver(), path);

        return new LocationImpl(r, 1);
    }    
}
