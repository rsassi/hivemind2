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

package org.apache.hivemind.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.definition.ConfigurationParserDefinition;
import org.apache.hivemind.definition.ConfigurationPointDefinition;
import org.apache.hivemind.definition.ModuleDefinition;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.definition.ServicePointDefinition;
import org.apache.hivemind.definition.UnresolvedExtension;
import org.apache.hivemind.definition.impl.ConfigurationParserDefinitionImpl;
import org.apache.hivemind.definition.impl.ConfigurationPointDefinitionImpl;
import org.apache.hivemind.schema.Schema;
import org.apache.hivemind.util.IdUtils;
import org.apache.hivemind.util.UniqueHashMap;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParser;
import org.apache.hivemind.xml.definition.impl.HiveMindSchemaParserConstructor;
import org.apache.hivemind.xml.definition.impl.XmlModuleDefinitionImpl;
import org.apache.hivemind.xml.definition.impl.XmlServicePointDefinitionImpl;

/**
 * Resolves the {@link UnresolvedExtension unresolved extensions} in all
 * xml modules of a {@link RegistryDefinition}.

 * @author Achim Huegen
 */
public class XmlExtensionResolver
{
    private static final Log LOG = LogFactory.getLog(XmlExtensionResolver.class);

    private ErrorHandler _errorHandler;
    
    private RegistryDefinition _definition;
    
    /**
     * Global map of all schemas defined in any model. Key is the qualified schema id, value
     * is an instance of {@link Schema}.
     */
    private Map _schemas = new HashMap();

    public XmlExtensionResolver(RegistryDefinition definition, ErrorHandler errorHandler)
    {
        _errorHandler = errorHandler;
        _definition = definition;
        buildGlobalSchemaMap();
    }

    /**
     * Resolves schema references via id from configuration points and services.
     * All added module descriptors are processed.
     */
    public void resolveSchemas()
    {
        Collection modules = _definition.getModules();
        
        for (Iterator iterModules = modules.iterator(); iterModules.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) iterModules.next();
            if (module instanceof XmlModuleDefinitionImpl) {
                XmlModuleDefinitionImpl xmlModule = (XmlModuleDefinitionImpl) module;
                resolveServicePointSchemas(xmlModule);
                resolveSchemaAssignments(xmlModule);
            }
        }
    }
    
    /**
     * Builds a map that contains all schemas of all defined xml modules.
     */
    public void buildGlobalSchemaMap()
    {
        Collection modules = _definition.getModules();
        for (Iterator iterModules = modules.iterator(); iterModules.hasNext();)
        {
            ModuleDefinition module = (ModuleDefinition) iterModules.next();
            if (module instanceof XmlModuleDefinitionImpl) {
                XmlModuleDefinitionImpl xmlModule = (XmlModuleDefinitionImpl) module;
                Collection schemas = xmlModule.getSchemas();
                for (Iterator iterSchemas = schemas.iterator(); iterSchemas.hasNext();)
                {
                    Schema schema = (Schema) iterSchemas.next();
                    String schemaId = IdUtils.qualify(module.getId(), schema.getId());
                    _schemas.put(schemaId, schema);
                }
            }
        }
    }

    private void resolveServicePointSchemas(ModuleDefinition sourceModule)
    {
        Collection points = sourceModule.getServicePoints();

        for (Iterator iterSp = points.iterator(); iterSp.hasNext();)
        {
            ServicePointDefinition spd = (ServicePointDefinition) iterSp.next();
            
            // Check type. In case of the hivemind core module non xml service points exist
            if (spd instanceof XmlServicePointDefinitionImpl) {
                XmlServicePointDefinitionImpl xspd = (XmlServicePointDefinitionImpl) spd;
    
                if (xspd.getParametersSchemaId() != null) {
                    ServicePointDefinition point = sourceModule.getServicePoint(xspd.getId());
    
                    XmlServicePointDefinitionImpl xmlServicePoint = (XmlServicePointDefinitionImpl) point;
                    Schema schema = findSchema(sourceModule, xspd.getParametersSchemaId(), xspd.getLocation());
                    if (schema == null) {
                        // Error-Handling has been done in findSchema already
                    } else {
                        xmlServicePoint.setParametersSchema(schema);
                        xmlServicePoint.setParametersCount(xspd.getParametersCount());
                    }
                }
            }
        }
    }
    
    /**
     * Adds schemas to configuration points for schemas that were assigned
     * to the configuration point subsequently by use of the "schema-assignment" attribute.
     * This is used if the original configuration point was defined in a non-xml module.
     */
    private void resolveSchemaAssignments(XmlModuleDefinitionImpl sourceModule)
    {
        Collection schemaAssignments = sourceModule.getSchemaAssignments();

        for (Iterator iterSa = schemaAssignments.iterator(); iterSa.hasNext();)
        {
            SchemaAssignment schemaAssignment = (SchemaAssignment) iterSa.next();

            String configurationPointId = IdUtils.qualify(sourceModule.getId(), schemaAssignment.getConfigurationId());
            ConfigurationPointDefinition cpd = _definition.getConfigurationPoint(configurationPointId);
            if (cpd == null) {
                throw new ApplicationRuntimeException(XmlImplMessages.unknownConfigurationPointOfSchemaAssignment(configurationPointId, schemaAssignment));
            }
            
            String schemaId = IdUtils.qualify(sourceModule.getId(), schemaAssignment.getSchemaId());
            Schema schema = getSchema(schemaId, sourceModule.getId(), schemaAssignment.getLocation());
            
            if (schema != null) {
                // TODO: check if schema has already been set
                // TODO: more type related error handling, check root type of schema
                
                // Add parser constructor with direct reference to schema
                ConfigurationParserDefinition parserDef = new ConfigurationParserDefinitionImpl(
                        sourceModule, schemaAssignment.getLocation(), HiveMindSchemaParser.INPUT_FORMAT_NAME, 
                        new HiveMindSchemaParserConstructor(schema));
                
                cpd.addParser(parserDef);
                
                // For backward compatibility change the configuration to Map if the schema uses a map too
                if (HashMap.class.getName().equals(schema.getRootElementClassName())
                        || UniqueHashMap.class.getName().equals(schema.getRootElementClassName())) {
                    // The schema assignments are mainly used for backward compatibility so we can 
                    // expect to deal here with a configuration point from the core or xml module.
                    // The cast prevents us from putting the setter into the public api of the ConfigurationPointDefinition
                    if (cpd instanceof ConfigurationPointDefinitionImpl) {
                        ((ConfigurationPointDefinitionImpl) cpd).setConfigurationTypeName(Map.class.getName());
                    }
                }
                
            }
        }
    }
    
    private Schema findSchema(ModuleDefinition module, String schemaId, Location location)
    {
        if (schemaId == null)
            return null;

        String moduleId = module.getId();
        String qualifiedId = IdUtils.qualify(moduleId, schemaId);

        return getSchema(qualifiedId, moduleId, location);
    }
    
    /**
     * @param qualifiedSchemaId
     * @param referencingModule the id of the module referencing the schema
     * @param location  the location the schema is referenced from
     * @return  null if schema wasn't found or is not visible
     */
    private Schema getSchema(String qualifiedSchemaId, String referencingModule, Location location)
    {
        Schema schema = (Schema) _schemas.get(qualifiedSchemaId);

        if (schema == null)
            _errorHandler
                    .error(LOG, XmlImplMessages.unableToResolveSchema(qualifiedSchemaId), location, null);
        else if (!schema.visibleToModule(referencingModule))
        {
            _errorHandler.error(
                    LOG,
                    XmlImplMessages.schemaNotVisible(qualifiedSchemaId, referencingModule),
                    location,
                    null);
            schema = null;
        }

        return schema;
    }
    
}
