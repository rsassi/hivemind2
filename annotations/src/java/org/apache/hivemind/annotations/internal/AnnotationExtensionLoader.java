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

package org.apache.hivemind.annotations.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.annotations.AnnotationsMessages;
import org.apache.hivemind.annotations.definition.processors.AnnotationProcessor;
import org.apache.hivemind.impl.ManifestReader;

/**
 * @author Achim Huegen
 */
public class AnnotationExtensionLoader
{
    private static final Log LOG = LogFactory.getLog(AnnotationExtensionLoader.class);
    public static final String MANIFEST = "META-INF/MANIFEST.MF";
    public static final String PROCESSOR_ATTRIBUTE_NAME = "annotation-definition-processors";

    private List _processors = new ArrayList();
    
    public AnnotationExtensionLoader(ClassResolver resolver)
    {
        String[] processorValues = ManifestReader.getAttributeValues(resolver, PROCESSOR_ATTRIBUTE_NAME);
        for (int i = 0; i < processorValues.length; i++)
        {
            String processorValue = processorValues[i];
            handleProcessorValue(resolver, processorValue);
        }
    }
    
    /**
     * @return  List with instances of {@link AnnotationProcessor}
     */
    public List getProcessors()
    {
        return _processors;
    }

    /**
     * Parse the processor list in an attribute and load all classes.
     */
    private void handleProcessorValue(ClassResolver resolver, String processors)
    {
        StringTokenizer tokenizer = new StringTokenizer(processors, ",");
        while (tokenizer.hasMoreTokens())
        {   
            String processorClassName = tokenizer.nextToken();
            loadProcessor(resolver, processorClassName);
        }
    }
    
    /**
     * Load a processor class and create an instance.
     * 
     * @param resolver
     * @param processorClassName
     */
    private void loadProcessor(ClassResolver resolver, String processorClassName)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Loading processor " + processorClassName);
        Object processor = null;
        try
        {
            Class processorClass = resolver.findClass(processorClassName);
            processor = processorClass.newInstance();
        }
        catch (Exception e)
        {
            Exception cause = e;
            if (e instanceof InvocationTargetException)
            {
                cause = (InvocationTargetException) e;
            }
            throw new ApplicationRuntimeException(AnnotationsMessages.unableToCreateAnnotationProcessor(processorClassName, e),
                    cause);
        }
        // Check type of processor
        if (!(processor instanceof AnnotationProcessor)) {
            throw new ApplicationRuntimeException(AnnotationsMessages.annotationProcessorWrongType(processorClassName, AnnotationProcessor.class));
        }
        
        _processors.add(processor);
    }

}