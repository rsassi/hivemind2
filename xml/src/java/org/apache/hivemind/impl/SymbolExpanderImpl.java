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

package org.apache.hivemind.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.Location;
import org.apache.hivemind.SymbolExpander;
import org.apache.hivemind.SymbolSource;
import org.apache.hivemind.order.Orderer;

/**
 * A simple parser used to identify symbols in a string and expand them via a
 * {@link org.apache.hivemind.SymbolSource}.
 * 
 * @author Howard Lewis Ship
 */
public class SymbolExpanderImpl implements SymbolExpander
{
    private static final Log LOG = LogFactory.getLog(SymbolExpanderImpl.class);

    private static final int STATE_START = 0;

    private static final int STATE_DOLLAR = 1;

    private static final int STATE_COLLECT_SYMBOL_NAME = 2;
    
    private ErrorHandler _errorHandler;

    private SymbolSource[] _symbolSources;

    private List _contributedSymbolSources;

    public SymbolExpanderImpl(ErrorHandler handler, List symbolSourceContributions)
    {
        _errorHandler = handler;
        _contributedSymbolSources = symbolSourceContributions;
    }

    public SymbolExpanderImpl(ErrorHandler handler, SymbolSource[] symbolSources)
    {
        _errorHandler = handler;
        _symbolSources = symbolSources;
    }
    
    private synchronized SymbolSource[] initContributedSymbolSources(List contributions)
    {
        SymbolSource[] symbolSources;
        
        Orderer o = new Orderer(LogFactory.getLog(SymbolExpander.class), _errorHandler, XmlImplMessages
                .symbolSourceContribution());

        Iterator i = contributions.iterator();
        while (i.hasNext())
        {
            SymbolSourceContribution c = (SymbolSourceContribution) i.next();

            o.add(c, c.getName(), c.getPrecedingNames(), c.getFollowingNames());
        }

        List sources = o.getOrderedObjects();

        int count = sources.size();

        symbolSources = new SymbolSource[count];

        for (int j = 0; j < count; j++)
        {
            SymbolSourceContribution c = (SymbolSourceContribution) sources.get(j);
            symbolSources[j] = c.getSource();
        }

        return symbolSources;
    }

    public String valueForSymbol(String name)
    {
        if (_symbolSources == null && _contributedSymbolSources != null) {
            // Load the contributions as late as possible since the
            // parsing of the xml contributions will trigger a recursive call.
            _symbolSources = initContributedSymbolSources(_contributedSymbolSources);
            _contributedSymbolSources = null;
        }
        
        for (int i = 0; i < _symbolSources.length; i++)
        {
            String value = _symbolSources[i].valueForSymbol(name);

            if (value != null)
                return value;
        }

        return null;
    }

    /**
     * @see org.apache.hivemind.SymbolExpander#expandSymbols(java.lang.String, org.apache.hivemind.Location)
     */
    public String expandSymbols(String text, Location location)
    {
        StringBuffer result = new StringBuffer(text.length());
        char[] buffer = text.toCharArray();
        int state = STATE_START;
        int blockStart = 0;
        int blockLength = 0;
        int symbolStart = -1;
        int symbolLength = 0;
        int i = 0;
        int braceDepth = 0;
        boolean anySymbols = false;

        while (i < buffer.length)
        {
            char ch = buffer[i];

            switch (state)
            {
                case STATE_START:

                    if (ch == '$')
                    {
                        state = STATE_DOLLAR;
                        i++;
                        continue;
                    }

                    blockLength++;
                    i++;
                    continue;

                case STATE_DOLLAR:

                    if (ch == '{')
                    {
                        state = STATE_COLLECT_SYMBOL_NAME;
                        i++;

                        symbolStart = i;
                        symbolLength = 0;
                        braceDepth = 1;

                        continue;
                    }

                    // Any time two $$ appear, it is collapsed down to a single $,
                    // but the next character is passed through un-interpreted (even if it
                    // is a brace).

                    if (ch == '$')
                    {
                        // This is effectively a symbol, meaning that the input string
                        // will not equal the output string.

                        anySymbols = true;

                        if (blockLength > 0)
                            result.append(buffer, blockStart, blockLength);

                        result.append(ch);

                        i++;
                        blockStart = i;
                        blockLength = 0;
                        state = STATE_START;

                        continue;
                    }

                    // The '$' was just what it was, not the start of a ${} expression
                    // block, so include it as part of the static text block.

                    blockLength++;

                    state = STATE_START;
                    continue;

                case STATE_COLLECT_SYMBOL_NAME:

                    if (ch != '}')
                    {
                        if (ch == '{')
                            braceDepth++;

                        i++;
                        symbolLength++;
                        continue;
                    }

                    braceDepth--;

                    if (braceDepth > 0)
                    {
                        i++;
                        symbolLength++;
                        continue;
                    }

                    // Hit the closing brace of a symbol.

                    // Degenerate case: the string "${}".

                    if (symbolLength == 0)
                        blockLength += 3;

                    // Append anything up to the start of the sequence (this is static
                    // text between symbol references).

                    if (blockLength > 0)
                        result.append(buffer, blockStart, blockLength);

                    if (symbolLength > 0)
                    {
                        String variableName = text.substring(symbolStart, symbolStart
                                + symbolLength);

                        result.append(expandSymbol(variableName, location));

                        anySymbols = true;
                    }

                    i++;
                    blockStart = i;
                    blockLength = 0;

                    // And drop into state start

                    state = STATE_START;

                    continue;
            }

        }

        // If get this far without seeing any variables, then just pass
        // the input back.

        if (!anySymbols)
            return text;

        // OK, to handle the end. Couple of degenerate cases where
        // a ${...} was incomplete, so we adust the block length.

        if (state == STATE_DOLLAR)
            blockLength++;

        if (state == STATE_COLLECT_SYMBOL_NAME)
            blockLength += symbolLength + 2;

        if (blockLength > 0)
            result.append(buffer, blockStart, blockLength);

        return result.toString();
    }

    private String expandSymbol(String name, Location location)
    {
        String value = valueForSymbol(name);

        if (value != null)
            return value;

        _errorHandler.error(LOG, XmlImplMessages.noSuchSymbol(name), location, null);

        return "${" + name + "}";
    }

}