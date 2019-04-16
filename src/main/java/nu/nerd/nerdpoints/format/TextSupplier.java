package nu.nerd.nerdpoints.format;

import java.util.function.Function;
import java.util.function.Supplier;

// ----------------------------------------------------------------------------
/**
 * A supplier of formatted text that only formats values when they change.
 * 
 * This is an optimisation on the basis that formatting a string is much more
 * expensive than caching the underlying value and checking whether it has
 * changed, so we should try to avoid the formatting step where possible.
 * 
 * The {@link #get()} implementation also doesn't bother to update the cached
 * value unless the text is actually needed.
 */
public class TextSupplier<T> implements Supplier<String> {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param getter the function that should be called to get the current value
     *        to format.
     * @param format the function that should be called to format the value into
     *        a String.
     */
    public TextSupplier(Supplier<T> getter, Function<T, String> format) {
        _getter = getter;
        _format = format;
    }

    // ------------------------------------------------------------------------
    /**
     * @see java.util.function.Supplier#get()
     */
    @Override
    public String get() {
        T newValue = _getter.get();
        if (_value == null || !_value.equals(newValue)) {
            // Invalidate cached text.
            _text = null;
        }
        _value = newValue;

        if (_text == null) {
            _text = _format.apply(_value);
        }
        return _text;
    }

    // ------------------------------------------------------------------------
    /**
     * The value used to generate the most recently formatted text.
     */
    protected T _value;

    /**
     * The function that should be called to get the current value to format.
     */
    protected Supplier<T> _getter;

    /**
     * The function that should be called to format the value into a String.
     */
    protected Function<T, String> _format;

    /**
     * The cached result of calling _format on _value, or _null if _format
     * should be called.
     */
    protected String _text;
} // class TextSupplier