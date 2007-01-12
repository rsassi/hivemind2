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

package org.apache.hivemind.impl;

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolExpander;
import org.apache.hivemind.TranslatorManager;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.schema.impl.AttributeModelImpl;
import org.apache.hivemind.schema.impl.ElementModelImpl;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.NullTranslator;
import org.apache.hivemind.schema.rules.ReadAttributeRule;
import org.apache.hivemind.schema.rules.ReadContentRule;
import org.apache.hivemind.test.HiveMindTestCase;
import org.easymock.MockControl;

/**
 * Tests for {@link org.apache.hivemind.schema.SchemaProcessor} and
 * {@link org.apache.hivemind.impl.SchemaElement}.
 * 
 * @author Howard Lewis Ship
 */
public class TestSchemaProcessor extends HiveMindTestCase
{

    public void testGetContentTranslator()
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();
        
        MockControl tmControl = newControl(TranslatorManager.class);
        TranslatorManager tm = (TranslatorManager) tmControl.getMock();

        MockControl symbolExpanderControl = newControl(SymbolExpander.class);
        SymbolExpander symbolExpander = (SymbolExpander) symbolExpanderControl.getMock();

        ElementModelImpl em = new ElementModelImpl("module");

        em.setElementName("fred");
        em.setContentTranslator("smart");

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl("module");
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);
        
        m.getRegistry();
        control.setReturnValue(registry);
        
        registry.getModule("module");
        registryControl.setReturnValue(m);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

        m.getService(SymbolExpander.class);
        control.setReturnValue(symbolExpander);

        symbolExpander.expandSymbols("flintstone", null);
        symbolExpanderControl.setReturnValue("flintstone");
        
        m.getService(TranslatorManager.class);
        control.setReturnValue(tm);

        tm.getTranslator("smart");
        tmControl.setReturnValue(new NullTranslator());
         
        replayControls();

        List dest = new ArrayList();
        p.process(dest, elements, m);

        assertEquals(1, dest.size());
        StringHolder h = (StringHolder) dest.get(0);

        assertEquals("flintstone", h.getValue());

        verifyControls();
    }

    public void testGetContentTranslatorUnspecified()
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();

        MockControl symbolExpanderControl = newControl(SymbolExpander.class);
        SymbolExpander symbolExpander = (SymbolExpander) symbolExpanderControl.getMock();

        ElementModelImpl em = new ElementModelImpl("module");

        em.setElementName("fred");
        // No content handler specified

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl("module");
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.setContent("flintstone");

        List elements = Collections.singletonList(element);
        
        m.getRegistry();
        control.setReturnValue(registry);
        
        registry.getModule(schema.getDefiningModuleId());
        registryControl.setReturnValue(m);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

        m.getService(SymbolExpander.class);
        control.setReturnValue(symbolExpander);

        symbolExpander.expandSymbols("flintstone", null);
        symbolExpanderControl.setReturnValue("flintstone");

        replayControls();

        List dest = new ArrayList();
        p.process(dest, elements, m);

        assertEquals(1, dest.size());
        StringHolder h = (StringHolder) dest.get(0);

        assertEquals("flintstone", h.getValue());

        verifyControls();
    }

    public void testGetAttributeTranslator()
    {
        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();

        MockControl tmControl = newControl(TranslatorManager.class);
        TranslatorManager tm = (TranslatorManager) tmControl.getMock();

        MockControl symbolExpanderControl = newControl(SymbolExpander.class);
        SymbolExpander symbolExpander = (SymbolExpander) symbolExpanderControl.getMock();

        ElementModelImpl em = new ElementModelImpl("module");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("wife");
        am.setTranslator("service");

        em.setElementName("fred");
        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule(StringHolderImpl.class.getName()));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setPropertyName("value");
        rule.setAttributeName("wife");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl("module");
        schema.addElementModel(em);

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("fred");
        element.addAttribute(new AttributeImpl("wife", "wilma"));

        List elements = Collections.singletonList(element);
        
        m.getRegistry();
        control.setReturnValue(registry);
        
        registry.getModule("module");
        registryControl.setReturnValue(m);

        m.resolveType("hivemind.test.services.impl.StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

        m.getService(SymbolExpander.class);
        control.setReturnValue(symbolExpander);

        symbolExpander.expandSymbols("wilma", null);
        symbolExpanderControl.setReturnValue("wilma");
        
        m.getService(TranslatorManager.class);
        control.setReturnValue(tm);

        tm.getTranslator("service");
        tmControl.setReturnValue(new NullTranslator());

        replayControls();

        List dest = new ArrayList();
        p.process(dest, elements, m);

        assertEquals(1, dest.size());
        StringHolder h = (StringHolder) dest.get(0);

        assertEquals("wilma", h.getValue());

        verifyControls();
    }

    /**
     * Tests for when the stack is empty.
     */
    public void testStackEmpty()
    {
        SchemaProcessor sp = new SchemaProcessorImpl(null, null);

        try
        {
            sp.pop();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {

        }

        try
        {
            sp.peek();
            unreachable();
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
        }

    }

    public void testKeyedElement()
    {
        ElementModelImpl em = new ElementModelImpl("module");
        
        MockControl tmControl = newControl(TranslatorManager.class);
        TranslatorManager tm = (TranslatorManager) tmControl.getMock();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();

        MockControl symbolExpanderControl = newControl(SymbolExpander.class);
        SymbolExpander symbolExpander = (SymbolExpander) symbolExpanderControl.getMock();

        em.setElementName("cartoon");
        em.setKeyAttribute("name");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("cartoon");

        em.addAttributeModel(am);
        
        em.addRule(new CreateObjectRule("StringHolderImpl"));
        
        ReadContentRule rule = new ReadContentRule();
        rule.setPropertyName("value");

        em.addRule(rule);
        
        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl("module");
        schema.addElementModel(em);

        MockControl control = newControl(Module.class);
        Module m = (Module) control.getMock();

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        ElementImpl element = new ElementImpl();
        element.setElementName("cartoon");
        element.setContent("${fred}");
        element.addAttribute(new AttributeImpl("name", "${flintstone}"));

        List elements = Collections.singletonList(element);

        m.getRegistry();
        control.setReturnValue(registry);
        
        registry.getModule("module");
        registryControl.setReturnValue(m);

        m.resolveType("StringHolderImpl");
        control.setReturnValue(StringHolderImpl.class);

        m.getService(SymbolExpander.class);
        control.setReturnValue(symbolExpander);
        
        symbolExpander.expandSymbols("${fred}", null);
        symbolExpanderControl.setReturnValue("fred");

        symbolExpander.expandSymbols("${flintstone}", null);
        symbolExpanderControl.setReturnValue("flintstone");

        MockControl tControl = newControl(Translator.class);
        Translator t = (Translator) tControl.getMock();

        tm.getTranslator("cartoon");
        tmControl.setReturnValue(t);

        m.getService(TranslatorManager.class);
        control.setReturnValue(tm);

        Object flintstoneKey = new Object();
        t.translate(m, Object.class, "flintstone", element.getLocation());
        tControl.setReturnValue(flintstoneKey);

        replayControls();

        Map dest = new HashMap();
        p.process(dest, elements, m);

        assertEquals(1, dest.size());
        StringHolder h = (StringHolder) dest.get(flintstoneKey);

        assertNotNull(h);
        assertEquals("fred", h.getValue());

        verifyControls();
    }

    /**
     * Test contributing 2 elements from 2 modules to a configuration-point with an attribute that
     * is marked unique and is translated by translator 'qualified-id'. Both contributed elements
     * use same untranslated value in the unique attribute. Fixes HIVEMIND-100.
     */
    public void testUniqueElement()
    {
        ElementModelImpl em = new ElementModelImpl("module");

        em.setElementName("cartoon");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("name");
        am.setTranslator("qualified-id");
        am.setUnique(true);

        em.addAttributeModel(am);

        em.addRule(new CreateObjectRule("StringHolderImpl"));

        ReadAttributeRule rule = new ReadAttributeRule();
        rule.setAttributeName("name");
        rule.setPropertyName("value");

        em.addRule(rule);

        em.addRule(new InvokeParentRule("addElement"));

        SchemaImpl schema = new SchemaImpl("module");
        schema.addElementModel(em);

        MockControl control1 = newControl(Module.class);
        Module m1 = (Module) control1.getMock();

        MockControl control2 = newControl(Module.class);
        Module m2 = (Module) control2.getMock();
        
        MockControl registryControl = newControl(RegistryInfrastructure.class);
        RegistryInfrastructure registry = (RegistryInfrastructure) registryControl.getMock();

        MockControl symbolExpanderControl = newControl(SymbolExpander.class);
        SymbolExpander symbolExpander = (SymbolExpander) symbolExpanderControl.getMock();

        SchemaProcessorImpl p = new SchemaProcessorImpl(null, schema);

        Location location1 = newLocation();
        ElementImpl element1 = new ElementImpl();
        element1.setElementName("cartoon");
        element1.addAttribute(new AttributeImpl("name", "flintstone"));
        element1.setLocation(location1);

        List elements1 = Collections.singletonList(element1);

        Location location2 = newLocation();
        ElementImpl element2 = new ElementImpl();
        element2.setElementName("cartoon");
        element2.addAttribute(new AttributeImpl("name", "flintstone"));
        element2.setLocation(location2);

        List elements2 = Collections.singletonList(element2);

        MockControl tControl1 = newControl(Translator.class);
        Translator t1 = (Translator) tControl1.getMock();
        
        MockControl tmControl = newControl(TranslatorManager.class);
        TranslatorManager tm = (TranslatorManager) tmControl.getMock();
        
        m1.getRegistry();
        control1.setReturnValue(registry);
        
        registry.getModule("module");
        registryControl.setReturnValue(m1);

        m2.getRegistry();
        control2.setReturnValue(registry);
        
        registry.getModule("module");
        registryControl.setReturnValue(m2);
        
        m1.resolveType("StringHolderImpl");
        control1.setReturnValue(StringHolderImpl.class);

        m1.getService(SymbolExpander.class);
        control1.setReturnValue(symbolExpander);
        
        symbolExpander.expandSymbols("flintstone", location1);
        symbolExpanderControl.setReturnValue("flintstone");
        
        m1.getService(TranslatorManager.class);
        control1.setReturnValue(tm);
        
        symbolExpander.expandSymbols("flintstone", location1);
        symbolExpanderControl.setReturnValue("flintstone");

        String flintstoneKeyModule1 = "m1.flintstone";
        t1.translate(m1, String.class, "flintstone", element1.getLocation());
        tControl1.setReturnValue(flintstoneKeyModule1);

        t1.translate(m1, Object.class, "flintstone", element1.getLocation());
        tControl1.setReturnValue(flintstoneKeyModule1);

        m2.resolveType("StringHolderImpl");
        control2.setReturnValue(StringHolderImpl.class);

        symbolExpander.expandSymbols("flintstone", location2);
        symbolExpanderControl.setReturnValue("flintstone");
        
        symbolExpander.expandSymbols("flintstone", location2);
        symbolExpanderControl.setReturnValue("flintstone");

        tm.getTranslator("qualified-id");
        tmControl.setReturnValue(t1);

        tm.getTranslator("qualified-id");
        tmControl.setReturnValue(t1);

        MockControl tControl2 = newControl(Translator.class);
        Translator t2 = (Translator) tControl2.getMock();

        tm.getTranslator("qualified-id");
        tmControl.setReturnValue(t2);
        
        String flintstoneKeyModule2 = "m2.flintstone";
        t2.translate(m2, String.class, "flintstone", element2.getLocation());
        tControl2.setReturnValue(flintstoneKeyModule2);
        
        t2.translate(m2, Object.class, "flintstone", element2.getLocation());
        tControl2.setReturnValue(flintstoneKeyModule2);

        tm.getTranslator("qualified-id");
        tmControl.setReturnValue(t2);


        replayControls();

        Map dest = new HashMap();
        p.process(dest, elements1, m1);
        p.process(dest, elements2, m2);

        assertEquals(2, dest.size());

        Set keys = new TreeSet();
        for (Iterator iter = dest.values().iterator(); iter.hasNext();)
        {
            StringHolderImpl element = (StringHolderImpl) iter.next();
            keys.add(element.getValue());
        }

        assertTrue(keys.contains(flintstoneKeyModule1));
        assertTrue(keys.contains(flintstoneKeyModule2));

        verifyControls();
    }

    public void testGetAttributeDefault()
    {
        ElementModelImpl em = new ElementModelImpl("module");
        em.setElementName("fred");

        AttributeModelImpl am = new AttributeModelImpl();
        am.setName("wife");
        am.setDefault("wilma");

        em.addAttributeModel(am);

        SchemaElement sel = new SchemaElement(null, em);

        assertEquals("wilma", sel.getAttributeDefault("wife"));
        assertNull(sel.getAttributeDefault("husband"));
    }
}