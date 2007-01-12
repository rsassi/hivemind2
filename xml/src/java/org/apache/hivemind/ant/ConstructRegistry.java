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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.hivemind.Resource;
import org.apache.hivemind.definition.RegistryDefinition;
import org.apache.hivemind.impl.RegistryBuilder;
import org.apache.hivemind.impl.XmlModuleReader;
import org.apache.hivemind.impl.HivemoduleProvider;
import org.apache.hivemind.util.FileResource;
import org.apache.hivemind.util.URLResource;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 * Reads some number of hivemodule deployment descriptors (specified as a fileset) and builds a
 * composite registry by simply concatinating them all. The resulting file is suitable for passing
 * through an XSLT processor to create documentation.
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
 * Note: Construct registry is only partially functional in the moment. 
 * Reason: The generation now works on the definition api which doesn't provide
 * data (for example Elements) which has been available in previous versions. 
 * Nevertheless its still xml specific (schemas) and to break this dependency one
 * had to invent a plugin mechanism that allows the different formats (xml, annotations)
 * to register specific renderers for the custom metadata. 
 * 
 * @author Howard Lewis Ship
 */
public class ConstructRegistry extends Task
{
    private File _output;

    private Path _modulesPath;

    public void execute() throws BuildException
    {
        if (_output == null)
            throw new BuildException("You must specify an output file");

        if (_modulesPath == null)
            throw new BuildException("You must specify a set of modules");

        RegistryDefinition registryDefinition = buildRegistry();
        Document registry = constructRegistryDocument(registryDefinition);

        log("Writing registry to " + _output);

        writeDocument(registry, _output);

    }

    private RegistryDefinition buildRegistry()
    {
        String[] paths = _modulesPath.list();
        int count = paths.length;
        
        File[] modules = new File[count];

        for (int i = 0; i < count; i++)
        {
            File f = new File(paths[i]);

            if (f.isDirectory())
                continue;

            modules[i] = f;
        }
        RegistryBuilder builder = new RegistryBuilder();
        buildRegistry(modules, builder.getRegistryDefinition());
        return builder.getRegistryDefinition();
    }
    
    private void buildRegistry(File[] modules, RegistryDefinition definition) throws BuildException
    {
        try
        {
            for (int i = 0; i < modules.length; i++)
                enqueue(modules[i], definition);
        }
        catch (Exception ex)
        {
            throw new BuildException(ex);
        }
    }

    /**
     * Queues up a single descriptor which may be a raw XML file, or a JAR (containing the XML
     * file).
     */
    private void enqueue(File file, RegistryDefinition definition) throws IOException
    {
        // This occurs when a bare directory is part of the classpath.

        if (file == null)
            return;

        if (file.getName().endsWith(".jar"))
        {
            enqueueJar(file, definition);
            return;
        }

        String path = file.getPath().replace('\\', '/');

        Resource r = new FileResource(path);

        enqueue(r, definition);
    }
    
    private void enqueueJar(File jarFile, RegistryDefinition definition) throws IOException
    {
        URL jarRootURL = new URL("jar:" + jarFile.toURL() + "!/" + HivemoduleProvider.HIVE_MODULE_XML);

        Resource jarResource = new URLResource(jarRootURL);
        enqueue(jarResource, definition);
    }
    
    private void enqueue(Resource resource, RegistryDefinition definition)
    {
        if (resource.getResourceURL() != null) {
            XmlModuleReader reader = new XmlModuleReader(definition);
            reader.readModule(resource);
        }
    }

    private Document constructRegistryDocument(RegistryDefinition registryDefinition) throws BuildException
    {
        try
        {
            RegistrySerializer generator = new RegistrySerializer();

            Document result = generator.createRegistryDocument(registryDefinition);

            return result;
        }
        catch (Exception ex)
        {
            throw new BuildException(ex);
        }
    }

    private void writeDocument(Document document, File file) throws BuildException
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            BufferedOutputStream buffered = new BufferedOutputStream(out);

            writeDocument(document, buffered);

            buffered.close();
        }
        catch (IOException ex)
        {
            throw new BuildException(
                    "Unable to write registry to " + file + ": " + ex.getMessage(), ex);
        }
    }

    private void writeDocument(Document document, OutputStream out) throws IOException
    {
        XMLSerializer serializer = new XMLSerializer(out, new OutputFormat(document, null, true));
        serializer.serialize(document);
    }

    public File getOutput()
    {
        return _output;
    }

    public void setOutput(File file)
    {
        _output = file;
    }

    public Path createModules()
    {
        _modulesPath = new Path(getProject());
        return _modulesPath;
    }

}