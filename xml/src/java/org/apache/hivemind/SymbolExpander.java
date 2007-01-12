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