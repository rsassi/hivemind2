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

package org.apache.hivemind;

import java.net.URL;

/**
 * An object which is used to resolve classes and class-path resources. This is needed because, in
 * an application server, different class loaders may be involved in loading different HiveMind
 * modules. For example, the HiveMind library may be on the system claasspath, and modules may
 * include EJBs and WARs, each loaded by a different classloader.
 * <p>
 * The class loader for the framework needs to be able to see resources in the application, but the
 * application's class loader is a descendent of the framework's class loader. To resolve this, we
 * need a 'hook', an instance that provides access to the application's class loader.
 * 
 * @author Howard Lewis Ship
 */

public interface ClassResolver
{
    /**
     * Forwarded, unchanged, to the class loader. Returns null if the resource is not found.
     */

    public URL getResource(String name);

    /**
     * Forwarded, to the the method <code>Class.forName(String, boolean, ClassLoader)</code>,
     * using the resolver's class loader.
     * <p>
     * Since 1.1, the type may include primitive types and arrays (of primitives or of objects).
     * 
     * @throws ApplicationRuntimeException
     *             on any error.
     */

    public Class findClass(String type);

    /**
     * Like {@link #findClass(String)}, but simply returns null if the class does not exist (i.e.,
     * if {@link ClassNotFoundException} is thrown). This is used in certain spots when (typically)
     * the exact package for a class is not known.
     */

    public Class checkForClass(String type);

    /**
     * Returns a {@link java.lang.ClassLoader} that can see all the classes the resolver can access.
     */

    public ClassLoader getClassLoader();
}