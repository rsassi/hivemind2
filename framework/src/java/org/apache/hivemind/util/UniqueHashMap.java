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

    private static final long serialVersionUID = -3961343404455706964L;

    public Object put(Object key, Object value)
    {
        if (containsKey(key)) {
            throw new ApplicationRuntimeException(UtilMessages.duplicateKeyInMap(key));
        }
        return super.put(key, value);
    }

}
