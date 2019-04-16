package nu.nerd.nerdpoints.format;

import java.util.HashMap;
import java.util.function.Supplier;

// ----------------------------------------------------------------------------
/**
 * Records the mapping from names to displayed values which are, in effect,
 * variables.
 * 
 * Displayed values are instances of Supplier<String> that are evaluated as
 * needed, so if a value is never referenced, the evaluation never occurs,
 * saving some CPU cycles.
 */
public class Scope {
    // ------------------------------------------------------------------------
    /**
     * Set the value associated with the name to the result of calling the
     * supplier.
     * 
     * @param name the key identifying the value in this Scope.
     * @param supplier supplies the String form of the value.
     */
    public void set(String name, Supplier<String> supplier) {
        _values.put(name, supplier);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the supplier associated with the specified name.
     * 
     * @param name the key.
     * @return the Supplier<String> that formats the value.
     */
    public Supplier<String> get(String name) {
        return _values.get(name);
    }

    // ------------------------------------------------------------------------
    /**
     * Map the specified name to fixed text.
     * 
     * @param name the key.
     * @param value the constant String value.
     */
    public void setText(String name, String value) {
        set(name, () -> value);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the value associated with name, or "%name%", if name has no
     * definition in this scope.
     * 
     * @return name the key.
     * @return the value associated with name, or "%name%", if name has no
     *         definition in this scope.
     */
    public String getText(String name) {
        Supplier<String> text = _values.get(name);
        return (text != null) ? text.get() : '%' + name + '%';
    }

    // ------------------------------------------------------------------------
    /**
     * Mapping from names to Supplier<String>s that format the corresponding
     * values.
     */
    protected HashMap<String, Supplier<String>> _values = new HashMap<>();
} // class Scope