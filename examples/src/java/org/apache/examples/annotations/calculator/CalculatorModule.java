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

package org.apache.examples.annotations.calculator;

import org.apache.examples.Adder;
import org.apache.examples.Calculator;
import org.apache.examples.Divider;
import org.apache.examples.Multiplier;
import org.apache.examples.Subtracter;
import org.apache.examples.impl.AdderImpl;
import org.apache.examples.impl.CalculatorImpl;
import org.apache.examples.impl.DividerImpl;
import org.apache.examples.impl.MultiplierImpl;
import org.apache.examples.impl.SubtracterImpl;
import org.apache.hivemind.annotations.AbstractAnnotatedModule;
import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Service;

/**
 * Example of an annotated module that creates a calculator.
 * 
 * @author Huegen
 */
@Module( id="calculator" )
public class CalculatorModule extends AbstractAnnotatedModule
{

    @Service( id="Adder" )
    public Adder getAdderService()
    {
        return new AdderImpl();
    }
    
    @Service( id="Subtracter" )
    public Subtracter getSubtractorService()
    {
        return new SubtracterImpl();
    }
    
    @Service( id="Multiplier" )
    public Multiplier getMultiplierService()
    {
        return new MultiplierImpl();
    }

    @Service( id="Divider" )
    public Divider getDividerService()
    {
        return new DividerImpl();
    }

    @Service( id="Calculator" )
    public Calculator getCalculatorService()
    {
        CalculatorImpl result = new CalculatorImpl();
        return autowireProperties(result);
    }

}
