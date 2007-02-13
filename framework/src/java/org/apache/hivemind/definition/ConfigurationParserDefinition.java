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

/**
 * Defines a parser for the processing of configuration data that is provided
 * in a textual format.  The parsed data is handled as {@link Contribution} to a 
 * configuration point. Each parser is associated with a certain input format,
 * which is specified by {@link #getInputFormat()}.
 * The parsing is done by an instance of {@link ConfigurationParser}
 * which is created by a {@link ConfigurationParserConstructor}.
 * 
 * @author Huegen
 */
public interface ConfigurationParserDefinition extends ExtensionDefinition
{
    /**
     * @return  the format of the data the parser can process 
     */
    public String getInputFormat();
    
    /**
     * @return  a factory for the construction of a parser instance
     */
    public ConfigurationParserConstructor getParserConstructor();
}
