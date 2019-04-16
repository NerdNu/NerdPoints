package nu.nerd.nerdpoints.format;

// ----------------------------------------------------------------------------
/**
 * A fixed point value with one digit to the right of the decimal point.
 * 
 * Only construction, equality, hashing and toString() operations are supported;
 * just the minimal functionality needed to format real numbers to one digit of
 * precision.
 */
public class Fixed1 {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param valueTimes10 the fixed point value as an integer that is 10x the
     *        real number.
     */
    public Fixed1(int valueTimes10) {
        _value = valueTimes10;
    }

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param value the real number value.
     */
    public Fixed1(float value) {
        _value = Math.round(10 * value);
    }

    // ------------------------------------------------------------------------
    /**
     * @see Object#toString()
     * 
     *      The returned String is padded with leading spaces to ensure the
     *      minimum length specified at construction time.
     */
    @Override
    public String toString() {
        _builder.setLength(0);
        _builder.append(_value / 10);
        _builder.append('.');
        _builder.append(Math.abs(_value) % 10);
        return _builder.toString();
    }

    // ------------------------------------------------------------------------
    /**
     * Format this number with the specified minimum length, padded with leading
     * spaces.
     * 
     * @param minLength the minimum length of the returned String.
     */
    public String toString(int minLength) {
        _builder.setLength(0);
        // Set up leading spaces to trim to width with substring().
        _builder.append(_value / 10);
        _builder.append('.');
        _builder.append(Math.abs(_value) % 10);
        if (_builder.length() < minLength) {
            int paddingLength = minLength - _builder.length();
            _builder.insert(0, PADDING.substring(PADDING.length() - paddingLength));
        }
        return _builder.toString();
    }

    // --------------------------------------------------------------------------
    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + _value;
        return result;
    }

    // --------------------------------------------------------------------------
    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Fixed1)) {
            return false;
        }
        Fixed1 other = (Fixed1) obj;
        if (_value != other._value) {
            return false;
        }
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * For padding with leading spaces; assumed to be shorter than this.
     */
    private static final String PADDING = "                                 ";

    /**
     * The fixed point value, computed as 10x the real number, rounded.
     */
    protected int _value;

    /**
     * Used to format strings.
     */
    protected StringBuilder _builder = new StringBuilder();
} // class Fixed1