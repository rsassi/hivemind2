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
