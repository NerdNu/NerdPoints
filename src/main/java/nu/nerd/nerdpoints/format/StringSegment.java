package nu.nerd.nerdpoints.format;

// ----------------------------------------------------------------------------
/**
 * A segment whose text is a fixed string.
 */
public class StringSegment implements Segment {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param text the literal text of the segment.
     */
    public StringSegment(String text) {
        _text = text;
    }

    // ------------------------------------------------------------------------
    /**
     * @see nu.nerd.nerdpoints.format.Segment#getText(Scope)
     */
    @Override
    public String getText(Scope scope) {
        return _text;
    }

    // ------------------------------------------------------------------------
    /**
     * The formatted text representation of this segment.
     */
    protected String _text;
} // class StringSegment