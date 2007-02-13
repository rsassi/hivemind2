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

package org.apache.hivemind.definition;

import java.io.InputStream;

/**
 * Parses configuration data that is provided in a textual format.
 * A new instance is created for each parsing operation.
 * 
 * @author Huegen
 */
public interface ConfigurationParser
{
    /**
     * Parses a configuration and returns the converted data.
     * @param context  context
     * @param data     the data to parse as stream. 
     * @return  the converted data.
     */
    public Object parse(ContributionContext context, InputStream data);
    
    /**
     * Parses a configuration and returns the converted data.
     * @param context  context
     * @param data     the data to parse. What kind of object is expected here is parser specific. 
     * @return  the converted data.
     */
    public Object parse(ContributionContext context, Object data);
}
