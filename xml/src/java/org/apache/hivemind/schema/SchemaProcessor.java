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

package org.apache.hivemind.schema;

import org.apache.hivemind.SymbolExpander;
import org.apache.hivemind.internal.Module;

/**
 * Object used when processing the elements contributed in an
 * {@link org.apache.hivemind.definition.Contribution}.
 * 
 * @author Howard Lewis Ship
 */
public interface SchemaProcessor
{

    /**
     * Pushes an object onto the processor's stack.
     */
    public void push(Object object);

    /**
     * Pops the top object off the stack and returns it.
     */

    public Object pop();

    /**
     * Peeks at the top object on the stack.
     */

    public Object peek();

    /**
     * Peeks at an object within the stack at the indicated depth.
     */

    public Object peek(int depth);

    /**
     * Returns the module which contributed the current elements being processed.
     */

    public Module getContributingModule();

    /**
     * Return the module which defined the schema.
     * 
     * @since 1.1
     */ 

    public String getDefiningModuleId();
    
    public Module getDefiningModule();

    /**
     * Returns the path to the current element in the form a sequence of element names separated
     * with slashes. This is most often used in error messages, to help identify the position of an
     * error.
     */

    public String getElementPath();

    /**
     * Returns a {@link org.apache.hivemind.schema.Translator} used to convert the content of the
     * current element. Will not return null.
     */

    public Translator getContentTranslator();

    /**
     * Returns the {@link org.apache.hivemind.schema.Translator} for a particular attribute of the
     * current element. Will not return null.
     */

    public Translator getAttributeTranslator(String attributeName);

    /**
     * @since 1.2
     */
    public String getAttributeDefault(String attributeName);

    /**
     * Returns the named {@link org.apache.hivemind.schema.Translator}.
     */

    public Translator getTranslator(String translator);
    
    /**
     * @return true  if the key-attribute attribute is used in all top level elements of the schema.
     *               The elements are placed in a map to maintain backward compatibility 
     */
    public boolean isInBackwardCompatibilityModeForMaps();
    
    /**
     * @return  the {@link SymbolExpander} used by the processor
     */
    public SymbolExpander getSymbolExpander();

}