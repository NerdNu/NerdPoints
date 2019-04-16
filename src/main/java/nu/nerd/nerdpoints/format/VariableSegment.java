package nu.nerd.nerdpoints.format;

// ----------------------------------------------------------------------------
/**
 * A Segment whose text representation is the formatted value of a variable in a
 * Scope.
 *
 * VariableSegments are references to variables that are lazily resolved in the
 * context of a {@link Scope} when {@link #getText()} is called.
 */
public class VariableSegment implements Segment {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param variableName the variable's name.
     */
    public VariableSegment(String variableName) {
        _variableName = variableName;
    }

    // ------------------------------------------------------------------------
    /**
     * @see nu.nerd.nerdpoints.format.Segment#getText(Scope)
     */
    @Override
    public String getText(Scope scope) {
        return scope.getText(_variableName);
    }

    // ------------------------------------------------------------------------
    /**
     * The variable's name.
     */
    protected String _variableName;
} // class VariableSegment