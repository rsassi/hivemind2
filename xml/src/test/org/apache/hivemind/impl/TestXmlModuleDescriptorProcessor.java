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

import hivemind.test.FrameworkTestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.Element;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.test.AggregateArgumentsMatcher;
import org.apache.hivemind.test.ArgumentMatcher;
import org.apache.hivemind.test.TypeMatcher;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParser;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParserConstructor;
import org.easymock.MockControl;

/**
 * Tests for {@link XmlModuleDescriptorProcessor}.
 * 
 * @author Knut Wannheden
 * @since 1.1
 */
public class TestXmlModuleDescriptorProcessor extends FrameworkTestCase
{
    /**
     * Tests if a schema referenced by id from a configuration point is resolved correctly, if it is
     * defined in another module.
     */
    public void testSchemaResolving()
    {
        SchemaImpl schema = new SchemaImpl("module");
        schema.setId("Baz");

        DefaultErrorHandler errorHandler = new DefaultErrorHandler();
        RegistryDefinition definition = new RegistryDefinition();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, errorHandler);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, errorHandler);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");

        zipZoop.addConfigurationPoint(cpd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition,
                errorHandler);
        processor.processModuleDescriptor(fooBar);
        processor.processModuleDescriptor(zipZoop);
        
        XmlExtensionResolver extensionResolver = new XmlExtensionResolver(definition, errorHandler);
        extensionResolver.resolveSchemas();

        ConfigurationPointDefinition point = definition.getConfigurationPoint("zip.zoop.Zap");
        
        ConfigurationParserDefinition parserDef = point.getParser(HiveMindSchemaParser.INPUT_FORMAT_NAME);
        assertNotNull(parserDef);
        
        assertEquals(parserDef.getParserConstructor().getClass(), HiveMindSchemaParserConstructor.class);

        HiveMindSchemaParserConstructor constructor = (HiveMindSchemaParserConstructor) parserDef.getParserConstructor();
        assertEquals(schema, constructor.getSchema());
    }

    /**
     * Tests if a schema referenced by id from a configuration point is not visible, if it is
     * defined as private in another module.
     */
    public void testSchemaNotVisible()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler errorHandler = (ErrorHandler) ehControl.getMock();

        Log log = LogFactory.getLog(XmlExtensionResolver.class);

        SchemaImpl schema = new SchemaImpl("foo.bar");
        schema.setId("Baz");
        schema.setVisibility(Visibility.PRIVATE);

        RegistryDefinition definition = new RegistryDefinition();

        Location l = newLocation();

        errorHandler.error(
                log,
                XmlImplMessages.schemaNotVisible("foo.bar.Baz", "zip.zoop"),
                l,
                null);

        replayControls();

        ModuleDescriptor fooBar = new ModuleDescriptor(null, errorHandler);
        fooBar.setModuleId("foo.bar");

        fooBar.addSchema(schema);

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, errorHandler);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition,
                errorHandler);
        processor.processModuleDescriptor(fooBar);
        processor.processModuleDescriptor(zipZoop);
        
        XmlExtensionResolver extensionResolver = new XmlExtensionResolver(definition, errorHandler);
        extensionResolver.resolveSchemas();

        verifyControls();
    }

    public void testSchemaNotFound()
    {
        ErrorHandler errorHandler = new DefaultErrorHandler();

        Log log = LogFactory.getLog(XmlModuleDescriptorProcessor.class);

        RegistryDefinition definition = new RegistryDefinition();

        Location l = newLocation();

        errorHandler.error(log, XmlImplMessages.unableToResolveSchema("foo.bar.Baz"), l, null);

        replayControls();

        ModuleDescriptor zipZoop = new ModuleDescriptor(null, errorHandler);
        zipZoop.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();
        cpd.setId("Zap");
        cpd.setContributionsSchemaId("foo.bar.Baz");
        cpd.setLocation(l);

        zipZoop.addConfigurationPoint(cpd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition,
                errorHandler);
        processor.processModuleDescriptor(zipZoop);
        
        XmlExtensionResolver extensionResolver = new XmlExtensionResolver(definition, errorHandler);
        extensionResolver.resolveSchemas();

        verifyControls();
    }

    private Element newElement(String name)
    {
        ElementImpl e = new ElementImpl();

        e.setElementName(name);

        return e;
    }

    public void testContributionConditionalExpressionTrue()
    {
        ErrorHandler errorHandler = new DefaultErrorHandler();

        RegistryDefinition definition = new RegistryDefinition();

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), errorHandler);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class " + Location.class.getName());

        cd.addElement(newElement("foo"));

        md.addContribution(cd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition,
                errorHandler);
        processor.processModuleDescriptor(md);

        ExtensionResolver extensionResolver = new ExtensionResolver(definition, new StrictErrorHandler());
        extensionResolver.resolveExtensions();

        ConfigurationPointDefinition configurationPoint = definition
                .getConfigurationPoint("zip.zoop.Fred");

        assertTrue(configurationPoint.getContributions().size() > 0);

        verifyControls();
    }

    public void testConditionalExpressionFalse()
    {
        ErrorHandler errorHandler = new DefaultErrorHandler();

        RegistryDefinition definition = new RegistryDefinition();

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), errorHandler);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("class foo.bar.Baz");

        cd.addElement(newElement("foo"));

        md.addContribution(cd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition,
                errorHandler);
        processor.processModuleDescriptor(md);

        ExtensionResolver extensionResolver = new ExtensionResolver(definition, new StrictErrorHandler());
        extensionResolver.resolveExtensions();

        ConfigurationPointDefinition configurationPoint = definition
                .getConfigurationPoint("zip.zoop.Fred");

        assertTrue(configurationPoint.getContributions().isEmpty());

        verifyControls();
    }

    public void testConditionalExpressionError()
    {
        MockControl ehControl = newControl(ErrorHandler.class);
        ErrorHandler eh = (ErrorHandler) ehControl.getMock();

        RegistryDefinition definition = new RegistryDefinition();

        Log log = LogFactory.getLog(XmlModuleDescriptorProcessor.class);

        Location location = newLocation();

        eh.error(
                log,
                "Unexpected token <AND> in expression 'and class foo'.",
                location,
                new RuntimeException());
        ehControl.setMatcher(new AggregateArgumentsMatcher(new ArgumentMatcher[]
        { null, null, null, new TypeMatcher() }));

        replayControls();

        ModuleDescriptor md = new ModuleDescriptor(getClassResolver(), eh);
        md.setModuleId("zip.zoop");

        ConfigurationPointDescriptor cpd = new ConfigurationPointDescriptor();

        cpd.setId("Fred");

        md.addConfigurationPoint(cpd);

        ContributionDescriptor cd = new ContributionDescriptor();
        cd.setConfigurationId("Fred");
        cd.setConditionalExpression("and class foo");
        cd.setLocation(location);

        cd.addElement(newElement("bar"));

        md.addContribution(cd);

        XmlModuleDescriptorProcessor processor = new XmlModuleDescriptorProcessor(definition, eh);
        processor.processModuleDescriptor(md);

        ExtensionResolver extensionResolver = new ExtensionResolver(definition, new StrictErrorHandler());
        extensionResolver.resolveExtensions();

        ConfigurationPointDefinition configurationPoint = definition
                .getConfigurationPoint("zip.zoop.Fred");

        assertTrue(configurationPoint.getContributions().isEmpty());

        verifyControls();
    }
}