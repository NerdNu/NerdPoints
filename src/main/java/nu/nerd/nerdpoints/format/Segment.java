package nu.nerd.nerdpoints.format;

// ----------------------------------------------------------------------------
/**
 * Represents one part of a formatted string with uniform properties (e.g. a
 * constant string, or text dynamically-generated from a single variable).
 */
public interface Segment {
    // ------------------------------------------------------------------------
    /**
     * Return the formatted text representation of this segment.
     * 
     * @return the formatted text representation of this segment.
     */
    public String getText(Scope scope);
}