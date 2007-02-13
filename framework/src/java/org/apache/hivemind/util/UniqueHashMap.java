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
