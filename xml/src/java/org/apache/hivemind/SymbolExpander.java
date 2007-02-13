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

package org.apache.hivemind;

import org.apache.hivemind.Location;

/**
 * Manages a list of {@link org.apache.hivemind.SymbolSource} and offers methods to 
 * lookup symbol values and identify symbols in a string and expand them
 * 
 * @author Howard Lewis Ship
 */
public interface SymbolExpander
{
    /**
     * Returns the value for the symbol, if this source can provide one.
     * Iterates all known {@link SymbolSource}s.
     * Returns null if no source can provide a value.
     */
    public String valueForSymbol(String name);

    /**
     * <p>
     * Identifies symbols in the text and expands them, using the {@link SymbolSource}. Returns the
     * modified text. May return text if text does not contain any symbols.
     * 
     * @param text
     *            the text to scan
     * @param location
     *            the location to report errors (undefined symbols)
     */
    public String expandSymbols(String text, Location location);
    
    
}