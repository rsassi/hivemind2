package org.apache.hivemind.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.conditional.EvaluationContextImpl;
import org.apache.hivemind.conditional.Node;
import org.apache.hivemind.conditional.Parser;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ImplementationDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.impl.ConfigurationParserDefinitionImpl;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.definition.impl.ContributionDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceImplementationDefinitionImpl;
import org.apache.hivemind.definition.impl.ServiceInterceptorDefinitionImpl;
import org.apache.hivemind.definition.impl.ServicePointDefinitionImpl;
import org.apache.hivemind.parse.ConfigurationPointDescriptor;
import org.apache.hivemind.parse.ContributionDescriptor;
import org.apache.hivemind.parse.DependencyDescriptor;
import org.apache.hivemind.parse.ImplementationDescriptor;
import org.apache.hivemind.parse.InstanceBuilder;
import org.apache.hivemind.parse.InterceptorDescriptor;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.SchemaAssignmentDescriptor;
import org.apache.hivemind.parse.ServicePointDescriptor;
import org.apache.hivemind.schema.impl.SchemaImpl;
import org.apache.hivemind.util.IdUtils;
import org.apache.hivemind.util.UniqueHashMap;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParser;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParserConstructor;
import org.apache.hivemind.xml.definition.impl.XmlContributionImpl;
import org.apache.hivemind.xml.definition.impl.XmlModuleDefinitionImpl;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;

/**
 * Adds the modules held by instances of {@link org.apache.hivemind.parse.ModuleDescriptor}
 * to a {@link org.apache.hivemind.definition.RegistryDefinition}.
 * That is the xml specific module definition is converted to the structure
 * defined in package {@link org.apache.hivemind.definition}.
 * 
 * @author Achim Huegen
 */
public class XmlModuleDescriptorProcessor 
{
    private static final Log _log = LogFactory.getLog(XmlModuleDescriptorProcessor.class);

    private ErrorHandler _errorHandler;

    private RegistryDefinition _registryDefinition;
    
    private Parser _conditionalExpressionParser;

    /**
     * Map of {@link ModuleDescriptor} keyed on module id.
     */
    private Map _moduleDescriptors = new HashMap();
    
    public XmlModuleDescriptorProcessor(RegistryDefinition registryDefinition, ErrorHandler errorHandler)
    {
        _registryDefinition = registryDefinition;
        _errorHandler = errorHandler;
    }

    /**
     * Adds all elements to the registry definition that are defined in a module descriptor. 
     */
    public void processModuleDescriptor(ModuleDescriptor md)
    {
        String id = md.getModuleId();
        
        if (_log.isDebugEnabled())
            _log.debug("Processing module " + id);

        _moduleDescriptors.put(id, md);

        XmlModuleDefinitionImpl module = new XmlModuleDefinitionImpl(id, md.getLocation(), md.getClassResolver(), md.getPackageName());
        
        addSchemas(module, md);
        
        addSchemaAssignments(module, md);

        addServicePoints(module, md);

        addConfigurationPoints(module, md);
        
        addImplementations(module, md);

        addContributions(module, md);

        addDependencies(module, md);
        
        _registryDefinition.addModule(module);
        
    }
    
    /**
     * Adds all module dependencies to the module definition.
     */
    private void addDependencies(ModuleDefinition module, ModuleDescriptor md)
    {
        int count = size(md.getDependencies());
        for (int i = 0; i < count; i++)
        {
            DependencyDescriptor dependency = (DependencyDescriptor) md.getDependencies().get(i);
            // TODO: DependencyDefinition mit Location , dependency.getLocation()
            module.addDependency(dependency.getModuleId());
        }
    }

    /**
     * Adds all schemas to the module definition.
     */
    private void addSchemas(XmlModuleDefinitionImpl module, ModuleDescriptor md)
    {
        for (Iterator schemas = md.getSchemas().iterator(); schemas.hasNext();)
        {
            SchemaImpl schema = (SchemaImpl) schemas.next();

            module.addSchema(schema.getId(), schema);
        }
    }
    
    /**
     * Adds all schema assignments to the module definition.
     */
    private void addSchemaAssignments(XmlModuleDefinitionImpl module, ModuleDescriptor md)
    {
        if (md.getSchemaAssignment() == null)
            return;
        
        for (Iterator assignments = md.getSchemaAssignment().iterator(); assignments.hasNext();)
        {
            SchemaAssignmentDescriptor sad = (SchemaAssignmentDescriptor) assignments.next();

            SchemaAssignment schemaAssignment = new SchemaAssignment(sad.getConfigurationId(), sad.getSchemaId(), sad.getLocation());
            module.addSchemaAssignment(schemaAssignment);
        }
    }
    
    /**
     * Adds all service points to the module definition.
     */
    private void addServicePoints(XmlModuleDefinitionImpl module, ModuleDescriptor md)
    {
        List services = md.getServicePoints();
        int count = size(services);

        for (int i = 0; i < count; i++)
        {
            ServicePointDescriptor sd = (ServicePointDescriptor) services.get(i);
            XmlServicePointDefinitionImpl servicePoint = new XmlServicePointDefinitionImpl(module, sd.getId(), sd.getLocation(), 
                    sd.getVisibility(), sd.getInterfaceClassName());

            // Store the schema if embedded,
            // Schemas are for service factories only
            if (sd.getParametersSchema() != null) {
                servicePoint.setParametersSchema(sd.getParametersSchema());
                servicePoint.setParametersCount(sd.getParametersCount());
            } else if (sd.getParametersSchemaId() != null ) {
                // referenced schemas are resolved in post processing
                servicePoint.setParametersSchemaId(sd.getParametersSchemaId());
            }
                
            
            module.addServicePoint(servicePoint);
                
            addInternalImplementations(module, servicePoint, sd);
        }
    }

    /**
     * Adds all service implementations to the module definition.
     */
    private void addImplementations(ModuleDefinition module, ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();

        List implementations = md.getImplementations();
        int count = size(implementations);

        for (int i = 0; i < count; i++)
        {
            ImplementationDescriptor impl = (ImplementationDescriptor) implementations.get(i);

            if (!includeContribution(impl.getConditionalExpression(), module, impl
                    .getLocation()))
                continue;

            String pointId = impl.getServiceId();
            String qualifiedId = IdUtils.qualify(moduleId, pointId);

            addImplementationAndInterceptors(module, qualifiedId, impl);
        }

    }

    /**
     * Adds one implementation and its interceptors to a module definition.
     */
    private void addImplementationAndInterceptors(ModuleDefinition sourceModule, String qualifiedPointId, ImplementationDescriptor id)
    {
        InstanceBuilder builder = id.getInstanceBuilder();
        List interceptors = id.getInterceptors();

        if (builder != null) {
            ServiceImplementationDefinitionImpl implementation = new ServiceImplementationDefinitionImpl(
                    sourceModule, builder.getLocation(), builder.createConstructor(sourceModule.getId()),
                    builder.getServiceModel(), false);
            sourceModule.addImplementation(qualifiedPointId, implementation); 
        }
        
        int count = size(interceptors);
        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor ind = (InterceptorDescriptor) interceptors.get(i);

            addInterceptor(sourceModule, qualifiedPointId, ind);
        }
    }

    /**
     * Adds internal service contributions; the contributions provided inplace with the service
     * definition.
     */
    private void addInternalImplementations(ModuleDefinition module, ServicePointDefinitionImpl point,
            ServicePointDescriptor spd)
    {
        InstanceBuilder builder = spd.getInstanceBuilder();
        List interceptors = spd.getInterceptors();
        String pointId = point.getId();

        if (builder == null && interceptors == null)
            return;

        if (builder != null) {
            ImplementationDefinition implementation = new ServiceImplementationDefinitionImpl(
                    module, builder.getLocation(), builder.createConstructor(module.getId()),
                    builder.getServiceModel(), false);
            point.addImplementation(implementation);
        }
        if (interceptors == null)
            return;

        int count = size(interceptors);

        for (int i = 0; i < count; i++)
        {
            InterceptorDescriptor id = (InterceptorDescriptor) interceptors.get(i);
            String qualifiedId = IdUtils.qualify(module.getId(), pointId);
            addInterceptor(module, qualifiedId, id);
        }
    }

    /**
     * Adds all configuratin points to a module definition.
     */
    private void addConfigurationPoints(XmlModuleDefinitionImpl module, ModuleDescriptor md)
    {
        List points = md.getConfigurationPoints();
        int count = size(points);

        for (int i = 0; i < count; i++)
        {
            ConfigurationPointDescriptor cpd = (ConfigurationPointDescriptor) points.get(i);

            ConfigurationPointDefinitionImpl configurationPoint = new ConfigurationPointDefinitionImpl(
                    module, cpd.getId(), cpd.getLocation(), cpd.getVisibility(), 
                    cpd.getType(), cpd.getCount());
            module.addConfigurationPoint(configurationPoint);
            
            // If schema is embedded we can add a parser now, otherwise it must
            // be resolved and processed later
            if (cpd.getContributionsSchema() != null) {
                // TODO: compare container class name and rootElementClassName
                if (HashMap.class.getName().equals(cpd.getContributionsSchema().getRootElementClassName())
                    || UniqueHashMap.class.getName().equals(cpd.getContributionsSchema().getRootElementClassName())) {
                    
                    configurationPoint.setConfigurationTypeName(Map.class.getName());
                }

                // Add parser constructor with direct reference to schema
                ConfigurationParserDefinition parserDef = new ConfigurationParserDefinitionImpl(
                        module, cpd.getContributionsSchema().getLocation(), HiveMindSchemaParser.INPUT_FORMAT_NAME, 
                        new HiveMindSchemaParserConstructor(cpd.getContributionsSchema()));
                
                configurationPoint.addParser(parserDef);
            } else if (cpd.getContributionsSchemaId() != null) {
                // Add schema assignment and resolve in post processing
                String qualifiedId = IdUtils.qualify(module.getId(), cpd.getId());
                SchemaAssignment schemaAssignment = new SchemaAssignment(qualifiedId, 
                        cpd.getContributionsSchemaId(), cpd.getLocation());
                module.addSchemaAssignment(schemaAssignment);
            }
        }
    }

    /**
     * Adds all contributions to a module definition.
     */
    private void addContributions(ModuleDefinition module, ModuleDescriptor md)
    {
        String moduleId = md.getModuleId();

        List contributions = md.getContributions();
        int count = size(contributions);

        for (int i = 0; i < count; i++)
        {
            ContributionDescriptor cd = (ContributionDescriptor) contributions.get(i);

            if (!includeContribution(cd.getConditionalExpression(), module, cd.getLocation()))
                continue;

            String pointId = cd.getConfigurationId();
            String qualifiedId = IdUtils.qualify(moduleId, pointId);
            
            ContributionDefinitionImpl contribution = new ContributionDefinitionImpl(module, cd.getLocation(),
                    new XmlContributionImpl(moduleId, cd.getElements()), false); 
            module.addContribution(qualifiedId, contribution);

        }
    }

    /**
     * Adds all interceptors to a module definition.
     */
    private void addInterceptor(ModuleDefinition module, String qualifiedPointId, InterceptorDescriptor id)
    {
        if (_log.isDebugEnabled())
            _log.debug("Adding " + id + " to service extension point " + qualifiedPointId);
        
        InvokeFactoryInterceptorConstructor constructor = new InvokeFactoryInterceptorConstructor(id.getLocation());
        constructor.setFactoryServiceId(id.getFactoryServiceId());
        constructor.setParameters(id.getParameters());
        constructor.setPrecedingInterceptorIds(id.getAfter());
        constructor.setFollowingInterceptorIds(id.getBefore());
        ServiceInterceptorDefinitionImpl interceptor = new ServiceInterceptorDefinitionImpl(
                module, id.getName(), id.getLocation(), constructor);
        module.addInterceptor(qualifiedPointId, interceptor);
    }

    /**
     * Filters a contribution based on an expression. Returns true if the expression is null, or
     * evaluates to true. Returns false if the expression if non-null and evaluates to false, or an
     * exception occurs evaluating the expression.
     * 
     * @param expression
     *            to parse and evaluate
     * @param location
     *            of the expression (used if an error is reported)
     */

    private boolean includeContribution(String expression, ModuleDefinition module, Location location)
    {
        if (expression == null)
            return true;

        if (_conditionalExpressionParser == null)
            _conditionalExpressionParser = new Parser();

        try
        {
            Node node = _conditionalExpressionParser.parse(expression);

            return node.evaluate(new EvaluationContextImpl(module.getClassResolver()));
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(_log, ex.getMessage(), location, ex);

            return false;
        }
    }
    
    private static int size(Collection c)
    {
        return c == null ? 0 : c.size();
    }

}
