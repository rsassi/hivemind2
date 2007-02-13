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

package org.apache.hivemind.service;

/**
 * Service that wires properties of object with services defined in the registry.
 * Different strategies are available. The standard strategies are defined
 * in {@link AutowiringStrategy}.
 * 
 * @author Achim Huegen
 */
public interface Autowiring
{
    /**
     * Autowires the properties of <code>target</code> defined in <code>propertyNames</code>.
     * All available strategies are tried until one strategy succeeds. 
     * @param target  the target object whose properties should be wired
     * @param propertyNames  the properties to wire
     * @return the wired target object
     */
    public Object autowireProperties(Object target, String[] propertyNames);
    
    /**
     * Autowires all writable properties of <code>target</code>.
     * All available strategies are tried until one strategy succeeds. 
     * @param target  the target object whose properties should be wired
     * @return the wired target object
     */
    public Object autowireProperties(Object target);
    
    /**
     * Autowires the properties of <code>target</code> defined in <code>propertyNames</code>
     * using a certain strategy.
     * @param strategy  name of the strategy to be used. Standard strategies are defined
     *                  in {@link AutowiringStrategy}
     * @param target  the target object whose properties should be wired
     * @param propertyNames  the properties to wire
     * @return the wired target object
     */
    public Object autowireProperties(String strategy, Object target, String[] propertyNames);
    
    /**
     * Autowires all writable properties of <code>target</code> using a certain strategy.
     * @param strategy  name of the strategy to be used. Standard strategies are defined
     *                  in {@link AutowiringStrategy}
     * @param target  the target object whose properties should be wired
     * @return the wired target object
     */
    public Object autowireProperties(String strategy, Object target);

}
