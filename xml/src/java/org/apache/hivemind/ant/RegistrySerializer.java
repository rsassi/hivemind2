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

package org.apache.hivemind.ant;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.Attribute;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ContributionDefinition;
import org.apache.hivemind.definition.ImplementationConstructor;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.Occurances;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.InterceptorDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.Visibility;
import org.apache.hivemind.impl.CreateClassServiceConstructor;
import org.apache.hivemind.impl.DefaultErrorHandler;
import org.apache.hivemind.impl.ExtensionResolver;
import org.apache.hivemind.impl.InvokeFactoryServiceConstructor;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.parse.AttributeMappingDescriptor;
import org.apache.hivemind.parse.ConversionDescriptor;
import org.apache.hivemind.schema.AttributeModel;
import org.apache.hivemind.schema.ElementModel;
import org.apache.hivemind.schema.Rule;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.schema.rules.CreateObjectRule;
import org.apache.hivemind.schema.rules.InvokeParentRule;
import org.apache.hivemind.schema.rules.PushAttributeRule;
import org.apache.hivemind.schema.rules.PushContentRule;
import org.apache.hivemind.schema.rules.ReadAttributeRule;
import org.apache.hivemind.schema.rules.ReadContentRule;
import org.apache.hivemind.schema.rules.SetModuleRule;
import org.apache.hivemind.schema.rules.SetParentRule;
import org.apache.hivemind.schema.rules.SetPropertyRule;
import org.apache.hivemind.util.IdUtils;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class serializes a set of {@link ModuleDefinition module descriptors} into a
 * {@link Document XML document}. The set of module descriptors to process is specified indirectly
 * by supplying one or several {@link ModuleDefinitionProvider} (see
 * {@link #addModuleDefinitionProvider(ModuleDefinitionProvider)}). In this respect this class is
 * used the same way as {@link org.apache.hivemind.impl.RegistryBuilder}. There is even a
 * corresponding {@link #createDefaultRegistryDocument() static method} to serialize the modules of
 * the default registry.
 * <p>
 * The resulting XML file does not conform to the hivemind module deployment descriptor schema. The
 * following changes occur:
 * <ul>
 * <li>The outermost element is &lt;registry&gt; (which contains a list of &lt;module&gt;)
 * <li>A unique id (unique within the file) is assigned to each &lt;module&gt;,
 * &lt;configuration-point&gt;, &lt;service-point&gt;, &lt;contribution&gt;, &tl;schema&gt; and
 * &lt;implementation&gt; (this is to make it easier to generate links and anchors)
 * <li>Unqualified ids are converted to qualified ids (whereever possible).
 * </ul>
 * 
 * HINT: In Version 2.0 this class is broken. 
 * Since registry definitions are no longer xml centric and a lot of other concepts are generalized too
 * (for example schemas are hidden by ConfigurationParsers now) it's much more difficult to
 * build a visualization of a registry. 
 * A possible solution: Build up a registry of renderers. Each renderer is responsible for 
 * rendering a certain type of definition object (ConfigurationPointDefinition, COnfigurationParserDefinition)
 * The renderers are contributed by the core modules (framework, xml, annotation) for example
 * via the manifest. 
 * The serializer then traverses a registry definition and searches for the renderer best
 * fitting the current element. The XmlContributionImpl could be rendered by a standard 
 * contribution renderer or if registered by a specialized xml contribution renderer.
 * All renderers must adhere to some general xml structur, they can't introduce new elements
 * because the xslt wouldn't know how to deal with them. 
 *  
 *   
 * @author Knut Wannheden
 * @since 1.1
 */
public class RegistrySerializer
{
    private Set _processedSchemas = new HashSet();

    private RegistryDefinition _registryDefinition;

    private ErrorHandler _handler;

    private Document _document;

    private ModuleDefinition _md;

    public RegistrySerializer()
    {
        _handler = new DefaultErrorHandler();
    }

    public Document createRegistryDocument(RegistryDefinition registryDefinition)
    {
        _registryDefinition = registryDefinition;
        ExtensionResolver resolver = new ExtensionResolver(_registryDefinition, _handler);
        resolver.resolveExtensions();
        
        DocumentBuilder builder = getBuilder();

        _document = builder.newDocument();

        Element registry = _document.createElement("registry");

//        processRegistryNatures(registry);

        _document.appendChild(registry);

        for (Iterator i = _registryDefinition.getModules().iterator(); i.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) i.next();

            Element moduleElement = getModuleElement(module);
            registry.appendChild(moduleElement);
            
        }

        return _document;
    }

    private Element getModuleElement(ModuleDefinition md)
    {
        _md = md;
        Element module = _document.createElement("module");

        module.setAttribute("id", md.getId());
        module.setAttribute("package", md.getPackageName());

//        module.appendChild(_document.createTextNode(md.getAnnotation()));

        addDependencies(module);

        addServicePoints(module);

        addConfigurationPoints(module);

        // No unresolved extensions left, since resolveExtensions was called on RegistryDefinition
//        addContributions(module);
//
//        addImplementations(module);

//        addSubModules(module);

        return module;
    }

    private void addDependencies(Element module)
    {
        Collection dependencies = _md.getDependencies();

        if (dependencies != null)
        {
            for (Iterator i = dependencies.iterator(); i.hasNext();)
            {
                String dd = (String) i.next();

                Element dependency = getDependencyElement(dd);

                module.appendChild(dependency);
            }
        }
    }

    private void addServicePoints(Element module)
    {
        Collection servicePoints = _md.getServicePoints();

        if (servicePoints != null)
        {
            for (Iterator i = servicePoints.iterator(); i.hasNext();)
            {
                ServicePointDefinition spd = (ServicePointDefinition) i.next();

                Element servicePoint = getServicePointElement(spd);

                module.appendChild(servicePoint);

            }
        }
    }

    private void addConfigurationPoints(Element module)
    {
        Collection configurationPoints = _md.getConfigurationPoints();

        if (configurationPoints != null)
        {
            for (Iterator i = configurationPoints.iterator(); i.hasNext();)
            {
                ConfigurationPointDefinition cpd = (ConfigurationPointDefinition) i.next();

                Element configurationPoint = getConfigurationPointElement(cpd);

                module.appendChild(configurationPoint);
            }
        }
    }

//    private void addContributions(Element module)
//    {
//        List contributions = _md.getContributions();
//
//        if (contributions != null)
//        {
//            for (Iterator i = contributions.iterator(); i.hasNext();)
//            {
//                ContributionDefinition cd = (ContributionDefinition) i.next();
//
//                Element contribution = getContributionElement(cd);
//
//                module.appendChild(contribution);
//            }
//        }
//    }

//    private void addImplementations(Element module)
//    {
//        List implementations = _md.getImplementations();
//
//        if (implementations != null)
//        {
//            for (Iterator i = implementations.iterator(); i.hasNext();)
//            {
//                ImplementationDefinition id = (ImplementationDefinition) i.next();
//
//                Element implementation = getImplementationElement(id);
//
//                module.appendChild(implementation);
//            }
//        }
//    }

//    private void processRegistryNatures(Element module)
//    {
//        XmlRegistryNature xmlNature = (XmlRegistryNature) _registryDefinition.getNature(XmlRegistryNature.class);
//        if (xmlNature != null) {
//            addSchemas(module, xmlNature);
//        }
//    }
//
//    private void addSchemas(Element module, XmlRegistryNature xmlNature)
//    {
//        Collection schemas = xmlNature.getSchemas();
//
//        for (Iterator i = schemas.iterator(); i.hasNext();)
//        {
//            SchemaImpl s = (SchemaImpl) i.next();
//
//            addSchema(module, s, "schema");
//        }
//    }

//    private void addSubModules(Element module)
//    {
//        List subModules = _md.getSubModules();
//
//        if (subModules != null)
//        {
//            for (Iterator i = subModules.iterator(); i.hasNext();)
//            {
//                SubModuleDefinition smd = (SubModuleDefinition) i.next();
//
//                Element subModule = getSubModuleElement(smd);
//
//                module.appendChild(subModule);
//            }
//        }
//    }

    private Element getDependencyElement(String dependsOn)
    {
        Element dependency = _document.createElement("dependency");

        dependency.setAttribute("module-id", dependsOn);

        return dependency;
    }

    private Element getServicePointElement(ServicePointDefinition spd)
    {
        Element servicePoint = _document.createElement("service-point");

        servicePoint.setAttribute("id", qualify(spd.getId()));
        servicePoint.setAttribute("interface", spd.getInterfaceClassName());
//        servicePoint.appendChild(_document.createTextNode(spd.getAnnotation()));

        if (spd.getVisibility() == Visibility.PRIVATE)
            servicePoint.setAttribute("visibility", "private");
        
        if (spd instanceof XmlServicePointDefinitionImpl)
            processXmlServicePoint(servicePoint, (XmlServicePointDefinitionImpl) spd);
        
        ImplementationDefinition ib = spd.getDefaultImplementation();

        if (ib != null)
        {
            Element instanceBuilder = getInstanceBuilderElement(ib, ib.getServiceConstructor());

            servicePoint.appendChild(instanceBuilder);
        }

        Collection interceptors = spd.getInterceptors();

        if (interceptors != null)
        {
            for (Iterator i = interceptors.iterator(); i.hasNext();)
            {
                InterceptorDefinition icd = (InterceptorDefinition) i.next();

                Element interceptor = getInterceptorElement(icd);

                servicePoint.appendChild(interceptor);
            }
        }

        return servicePoint;
    }

    private void processXmlServicePoint(Element servicePointElement, XmlServicePointDefinitionImpl xmlServicePoint)
    {
        if (xmlServicePoint.getParametersCount() != Occurances.REQUIRED)
            servicePointElement.setAttribute("parameters-occurs", xmlServicePoint.getParametersCount().getName()
                    .toLowerCase());
        if (xmlServicePoint.getParametersSchema() != null)
            addSchema(servicePointElement, (SchemaImpl) xmlServicePoint.getParametersSchema(), "parameters-schema");
//        else if (xmlNature.getParametersSchemaId() != null)
//            servicePoint.setAttribute("parameters-schema-id", qualify(spd.getParametersSchemaId()));
        
    }

    private Element getConfigurationPointElement(ConfigurationPointDefinition cpd)
    {
        Element configurationPoint = _document.createElement("configuration-point");

        configurationPoint.setAttribute("id", qualify(cpd.getId()));
        if (cpd.getVisibility() == Visibility.PRIVATE)
            configurationPoint.setAttribute("visibility", "private");

//        configurationPoint.appendChild(_document.createTextNode(cpd.getAnnotation()));

//        processConfigurationPointNatures(configurationPoint, cpd);
        
        Collection contributions = cpd.getContributions();
        for (Iterator iter = contributions.iterator(); iter.hasNext();)
        {
            ContributionDefinition contribution = (ContributionDefinition) iter.next();
            Element contributionElement = getContributionElement(contribution, cpd.getId());

            configurationPoint.appendChild(contributionElement);
            
        }
        
        return configurationPoint;
    }
    
//    private void addConfigurationPointSchemas(Element configurationPoint, XmlConfigurationPointNature xmlNature)
//    {
//        if (xmlNature.getSchema() != null)
//            addSchema(configurationPoint, (SchemaImpl) xmlNature.getSchema(), "schema");
////        else if (cpd.getContributionsSchemaId() != null)
////            configurationPoint.setAttribute("schema-id", qualify(cpd.getContributionsSchemaId()));
//    }

    private Element getContributionElement(ContributionDefinition cd, String configurationPointId)
    {
        Element contribution = _document.createElement("contribution");

        contribution.setAttribute("configuration-id", qualify(configurationPointId));

//        if (cd.getConditionalExpression() != null)
//            contribution.setAttribute("if", cd.getConditionalExpression());

//        List parameters = cd.getElements();
//
//        if (parameters != null)
//        {
//            for (Iterator i = parameters.iterator(); i.hasNext();)
//            {
//                org.apache.hivemind.Element parameter = (org.apache.hivemind.Element) i.next();
//
//                Element element = getParamterElement(parameter);
//
//                contribution.appendChild(element);
//            }
//        }

//        contribution.appendChild(_document.createTextNode(cd.getAnnotation()));

        return contribution;
    }

//    private Element getImplementationElement(ImplementationDefinition id)
//    {
//        Element implementation = _document.createElement("implementation");
//
//        implementation.setAttribute("service-id", qualify(id.getServiceId()));
//
//        if (id.getConditionalExpression() != null)
//            implementation.setAttribute("if", id.getConditionalExpression());
//
//        implementation.appendChild(_document.createTextNode(id.getAnnotation()));
//
//        ServiceImplementationConstructor ib = id.getServiceConstructor();
//
//        if (ib != null)
//        {
//            Element instanceBuilder = getInstanceBuilderElement(id, ib);
//
//            implementation.appendChild(instanceBuilder);
//        }
//
//        return implementation;
//    }

//    private Element getSubModuleElement(SubModuleDefinition smd)
//    {
//        Element subModule = _document.createElement("sub-module");
//
//        subModule.setAttribute("descriptor", smd.getDefinition().getPath());
//
//        return subModule;
//    }

    private Element getInstanceBuilderElement(ImplementationDefinition id, ImplementationConstructor ib)
    {
        Element instanceBuilder;
        
        if (ib instanceof CreateClassServiceConstructor)
        {
            CreateClassServiceConstructor cid = (CreateClassServiceConstructor) ib;
            instanceBuilder = _document.createElement("create-instance");

            instanceBuilder.setAttribute("class", cid.getInstanceClassName());
            if (!id.getServiceModel().equals("singleton"))
                instanceBuilder.setAttribute("model", id.getServiceModel());
        }
        else 
        {
            InvokeFactoryServiceConstructor ifd = (InvokeFactoryServiceConstructor) ib;
            instanceBuilder = _document.createElement("invoke-factory");

            if (!ifd.getFactoryServiceId().equals("hivemind.BuilderFactory"))
                instanceBuilder.setAttribute("service-id", qualify(ifd.getFactoryServiceId()));
            if (id.getServiceModel() != null)
                instanceBuilder.setAttribute("model", id.getServiceModel());

            List parameters = ifd.getParameters();

            if (parameters != null)
            {
                for (Iterator i = parameters.iterator(); i.hasNext();)
                {
                    org.apache.hivemind.Element parameter = (org.apache.hivemind.Element) i.next();

                    Element element = getParamterElement(parameter);

                    instanceBuilder.appendChild(element);
                }
            }
        }

        return instanceBuilder;
    }

    private Element getInterceptorElement(InterceptorDefinition icd)
    {
        Element interceptor = _document.createElement("interceptor");

//        interceptor.setAttribute("service-id", qualify(icd.getName()));
//        if (icd.getBefore() != null)
//            interceptor.setAttribute("before", icd.getBefore());
//        if (icd.getAfter() != null)
//            interceptor.setAttribute("after", icd.getAfter());
        return interceptor;
    }

    private Element getParamterElement(org.apache.hivemind.Element parameter)
    {
        Element element = _document.createElement(parameter.getElementName());

        List attributes = parameter.getAttributes();

        for (Iterator i = attributes.iterator(); i.hasNext();)
        {
            Attribute attribute = (Attribute) i.next();

            element.setAttribute(attribute.getName(), attribute.getValue());
        }

        List elements = parameter.getElements();

        for (Iterator i = elements.iterator(); i.hasNext();)
        {
            org.apache.hivemind.Element nestedParameter = (org.apache.hivemind.Element) i.next();

            element.appendChild(getParamterElement(nestedParameter));
        }

        return element;
    }

    private void addSchema(Element container, SchemaImpl s, String elementName)
    {
        if (_processedSchemas.contains(s))
            return;

        Element schema = _document.createElement(elementName);

        if (s.getId() != null)
            schema.setAttribute("id", qualify(s.getId()));

        if (s.getVisibility() == Visibility.PRIVATE)
            schema.setAttribute("visibility", "private");

        schema.appendChild(_document.createTextNode(s.getAnnotation()));

        for (Iterator j = s.getElementModel().iterator(); j.hasNext();)
        {
            ElementModel em = (ElementModel) j.next();

            Element element = getElementElement(em);

            schema.appendChild(element);
        }

        container.appendChild(schema);

        _processedSchemas.add(s);
    }

    private Element getRulesElement(ElementModel em)
    {
        Element rules = _document.createElement("rules");

        for (Iterator i = em.getRules().iterator(); i.hasNext();)
        {
            Rule r = (Rule) i.next();

            Element rule = null;

            if (r instanceof CreateObjectRule)
            {
                CreateObjectRule cor = (CreateObjectRule) r;
                rule = _document.createElement("create-object");

                rule.setAttribute("class", cor.getClassName());
            }
            else if (r instanceof InvokeParentRule)
            {
                InvokeParentRule ipr = (InvokeParentRule) r;
                rule = _document.createElement("invoke-parent");

                rule.setAttribute("method", ipr.getMethodName());
                if (ipr.getDepth() != 1)
                    rule.setAttribute("depth", Integer.toString(ipr.getDepth()));
            }
            else if (r instanceof PushAttributeRule)
            {
                PushAttributeRule par = (PushAttributeRule) r;
                rule = _document.createElement("push-attribute");

                rule.setAttribute("attribute", par.getAttributeName());
            }
            else if (r instanceof PushContentRule)
            {              
                rule = _document.createElement("push-content");
            }
            else if (r instanceof ReadAttributeRule)
            {
                ReadAttributeRule rar = (ReadAttributeRule) r;
                rule = _document.createElement("read-attribute");

                rule.setAttribute("property", rar.getPropertyName());
                rule.setAttribute("attribute", rar.getAttributeName());
                if (!rar.getSkipIfNull())
                    rule.setAttribute("skip-if-null", "false");
                if (rar.getTranslator() != null)
                    rule.setAttribute("translator", rar.getTranslator());
            }
            else if (r instanceof ReadContentRule)
            {
                ReadContentRule rcr = (ReadContentRule) r;
                rule = _document.createElement("read-content");

                rule.setAttribute("property", rcr.getPropertyName());
            }
            else if (r instanceof SetModuleRule)
            {
                SetModuleRule smr = (SetModuleRule) r;
                rule = _document.createElement("set-module");

                rule.setAttribute("property", smr.getPropertyName());
            }
            else if (r instanceof SetParentRule)
            {
                SetParentRule spr = (SetParentRule) r;
                rule = _document.createElement("set-parent");

                rule.setAttribute("property", spr.getPropertyName());
            }
            else if (r instanceof SetPropertyRule)
            {
                SetPropertyRule spr = (SetPropertyRule) r;
                rule = _document.createElement("set-property");

                rule.setAttribute("property", spr.getPropertyName());
                rule.setAttribute("value", spr.getValue());
            }
            else if (r instanceof ConversionDescriptor)
            {
                ConversionDescriptor cd = (ConversionDescriptor) r;
                rule = _document.createElement("conversion");

                rule.setAttribute("class", cd.getClassName());
                if (!cd.getParentMethodName().equals("addElement"))
                    rule.setAttribute("parent-method", cd.getParentMethodName());

                for (Iterator j = cd.getAttributeMappings().iterator(); j.hasNext();)
                {
                    AttributeMappingDescriptor amd = (AttributeMappingDescriptor) j.next();

                    Element map = _document.createElement("map");

                    map.setAttribute("attribute", amd.getAttributeName());
                    map.setAttribute("property", amd.getPropertyName());

                    rule.appendChild(map);
                }
            }
            else
            {
                rule = _document.createElement("custom");

                rule.setAttribute("class", r.getClass().getName());
            }

            if (rule != null)
                rules.appendChild(rule);
        }
        return rules;
    }

    private Element getElementElement(ElementModel em)
    {
        Element element = _document.createElement("element");
        element.setAttribute("name", em.getElementName());

        element.appendChild(_document.createTextNode(em.getAnnotation()));

        for (Iterator i = em.getAttributeModels().iterator(); i.hasNext();)
        {
            AttributeModel am = (AttributeModel) i.next();

            Element attribute = getAttributeElement(am);

            element.appendChild(attribute);
        }

        for (Iterator i = em.getElementModel().iterator(); i.hasNext();)
        {
            ElementModel nestedEm = (ElementModel) i.next();

            Element nestedElement = getElementElement(nestedEm);

            element.appendChild(nestedElement);
        }

        if (!em.getRules().isEmpty())
        {
            Element rules = getRulesElement(em);

            element.appendChild(rules);
        }

        return element;
    }

    private Element getAttributeElement(AttributeModel am)
    {
        Element attribute = _document.createElement("attribute");

        attribute.setAttribute("name", am.getName());
        if (am.isRequired())
            attribute.setAttribute("required", "true");
        if (am.isUnique())
            attribute.setAttribute("unique", "true");
        if (am.getDefault() != null)
            attribute.setAttribute("default", am.getDefault());
        if (!am.getTranslator().equals("smart"))
            attribute.setAttribute("translator", am.getTranslator());

        attribute.appendChild(_document.createTextNode(am.getAnnotation()));

        return attribute;
    }

    private String qualify(String id)
    {
        return IdUtils.qualify(_md.getId(), id);
    }

    private DocumentBuilder getBuilder()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        factory.setIgnoringComments(true);

        try
        {
            return factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e)
        {
            throw new ApplicationRuntimeException(e);
        }
    }

    public static Document createDefaultRegistryDocument()
    {
        RegistryBuilder builder = new RegistryBuilder();
        builder.autoDetectModules();

        RegistrySerializer serializer = new RegistrySerializer();

        return serializer.createRegistryDocument(builder.getRegistryDefinition());
    }
}