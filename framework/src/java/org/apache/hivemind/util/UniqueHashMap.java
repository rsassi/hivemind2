package org.apache.hivemind.util;

import java.util.HashMap;

import org.apache.hivemind.ApplicationRuntimeException;

/**
 * Specialized map that guarantees uniqueness of key values.
 * 
 * @author Achim Huegen
 */
public class UniqueHashMap extends HashMap
{

    public Object put(Object key, Object value)
    {
        if (containsKey(key)) {
            // TODO annotations: Exception-Handling
            throw new ApplicationRuntimeException("Key '" + key + "' already contained in map");
        }
        return super.put(key, value);
    }

}
