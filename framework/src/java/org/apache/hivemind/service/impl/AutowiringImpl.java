package org.apache.hivemind.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.internal.RegistryInfrastructure;
import org.apache.hivemind.service.Autowiring;
import org.apache.hivemind.service.AutowiringStrategy;
import org.apache.hivemind.util.PropertyUtils;

/**
 * Implementation of {@link Autowiring}.
 * Skips properties of primitive type and the standard java data types (String, Double etc.).
 * Properties that are already assigned are skipped too.
 * Delegates the autowiring to implementations of {@link AutowiringStrategy}.
 * If errors occur they are passed to an {@link ErrorHandler}. Depending on its
 * implementation the wiring either continues with the next property or an exception
 * is thrown. 
 * 
 * @author Achim Huegen
 */
public class AutowiringImpl implements Autowiring
{
    private static final Log LOG = LogFactory.getLog(AutowiringImpl.class);

    private Map _strategies = new TreeMap();

    private RegistryInfrastructure _registry;
    
    private ErrorHandler _errorHandler;
    
    private static Set SKIPPED_PROPERTY_TYPES = new HashSet();
    
    static {
        SKIPPED_PROPERTY_TYPES.add(String.class);
        SKIPPED_PROPERTY_TYPES.add(Double.class);
        SKIPPED_PROPERTY_TYPES.add(Integer.class);
        SKIPPED_PROPERTY_TYPES.add(Float.class);
        SKIPPED_PROPERTY_TYPES.add(Byte.class);
        SKIPPED_PROPERTY_TYPES.add(Short.class);
        SKIPPED_PROPERTY_TYPES.add(Character.class);
    }
    
    /**
     * @param context
     * @param strategyContributions  list with instances of {@link AutowiringStrategyContribution}
     */
    public AutowiringImpl(RegistryInfrastructure registry, List strategyContributions, ErrorHandler errorHandler)
    {
        _registry = registry;
        _errorHandler = errorHandler;
        // Add the strategies in default order to map
        for (Iterator iter = strategyContributions.iterator(); iter.hasNext();)
        {
            AutowiringStrategyContribution c = (AutowiringStrategyContribution) iter.next();
            _strategies.put(c.getName(), c.getStrategy());
        }
    }

    /**
     * @see org.apache.hivemind.service.Autowiring#autowireProperties(java.lang.Object)
     */
    public Object autowireProperties(Object target)
    {
        Set writeablePropertiesSet = new HashSet(PropertyUtils.getWriteableProperties(target));
        String[] writableProperties = (String[]) writeablePropertiesSet.toArray(new String[writeablePropertiesSet.size()]);
        return autowireProperties(target, writableProperties);
    }
    
    /**
     * @see org.apache.hivemind.service.Autowiring#autowireProperties(java.lang.Object, java.lang.String[])
     */
    public Object autowireProperties(Object target, String[] propertyNames)
    {
        for (int i = 0; i < propertyNames.length; i++)
        {
            String propertyName = propertyNames[i];
            if (isPropertyWirable(target, propertyName)) {
                autowirePropertyAllStrategies(target, propertyName);
            }
        }
        return target;
    }

    /**
     * Wires a single property by calling all available strategies in the configured order
     * until a strategy signals that the property has been wired. 
     * 
     * @param target
     * @param propertyName
     */
    private void autowirePropertyAllStrategies(Object target, String propertyName)
    {
        try
        {
            for (Iterator iter = _strategies.values().iterator(); iter.hasNext();)
            {
                AutowiringStrategy strategy = (AutowiringStrategy) iter.next();
                boolean isWired = strategy.autowireProperty(_registry, target, propertyName);
                // Stop if strategy has wired the property 
                if (isWired)
                    break;
            }
        }
        catch (Exception ex)
        {
            _errorHandler.error(
                    LOG,
                    ServiceMessages.autowirePropertyFailure(propertyName, target.getClass(), ex),
                    null,
                    ex);
        }
    }
    
    /**
     * @see org.apache.hivemind.service.Autowiring#autowireProperties(java.lang.String, java.lang.Object)
     */
    public Object autowireProperties(String strategy, Object target)
    {
        Set writeablePropertiesSet = new HashSet(PropertyUtils.getWriteableProperties(target));
        String[] writableProperties = (String[]) writeablePropertiesSet.toArray(new String[writeablePropertiesSet.size()]);
        return autowireProperties(strategy, target, writableProperties);
    }

    /**
     * @see org.apache.hivemind.service.Autowiring#autowireProperties(java.lang.String, java.lang.Object, java.lang.String[])
     */
    public Object autowireProperties(String strategyName, Object target, String[] propertyNames)
    {
        for (int i = 0; i < propertyNames.length; i++)
        {
            String propertyName = propertyNames[i];
            if (isPropertyWirable(target, propertyName)) {
                autowireProperty(strategyName, target, propertyName);
            }
        }
        return target;
    }
    
    /**
     * @return  true if the property is wirable. Primitive types and the types in
     *  SKIPPED_PROPERTY_TYPES are ignored. If the property is already assigned it is
     *  ignored too.
     */
    private boolean isPropertyWirable(Object target, String propertyName)
    {
        Class propertyType = PropertyUtils.getPropertyType(target, propertyName);
        if (propertyType.isPrimitive() || SKIPPED_PROPERTY_TYPES.contains(propertyType)) {
            return false;
        } else {
            // Don't wire if value is already assigned
            if (PropertyUtils.isReadable(target, propertyName))
                return PropertyUtils.read(target, propertyName) == null;
            else return true;
        }
    }

    /**
     * Wires a single property by using the strategy <code>strategyName</code> 
     * @param strategyName
     * @param target
     * @param propertyName
     * @return true if wiring succeeded
     */
    private boolean autowireProperty(String strategyName, Object target, String propertyName)
    {
        try
        {
           AutowiringStrategy strategy = (AutowiringStrategy) _strategies.get(strategyName);
           if (strategy == null) {
               throw new ApplicationRuntimeException(ServiceMessages.unknownStrategy(strategyName));
           }
           return strategy.autowireProperty(_registry, target, propertyName);
        }
        catch (Exception ex)
        {
            _errorHandler.error(
                    LOG,
                    ServiceMessages.autowirePropertyFailure(propertyName, target.getClass(), ex),
                    null,
                    ex);
        }
        return false;
    }

}
