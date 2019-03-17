package nu.nerd.nerdpoints;

import java.util.Map;
import java.util.Map.Entry;

// ----------------------------------------------------------------------------
/**
 * General utility functions.
 */
public class Util {
    // ------------------------------------------------------------------------
    /**
     * Replace all variable references in the string s according to the
     * replacements array.
     * 
     * @param s a string that may contain variable references of the form
     *        %name%.
     * @param replacements an array of alternating variable names (sans "%") and
     *        their corresponding string values.
     * @return the string with all variable references replaced by their values.
     */
    public static String replace(String s, String... replacements) {
        if (replacements.length % 2 != 0) {
            throw new IllegalArgumentException("replacements should contain an even number of Strings");
        }
        for (int i = 0; i < replacements.length; i += 2) {
            s = s.replace('%' + replacements[i] + '%', replacements[i + 1]);
        }
        return s;
    }

    // ------------------------------------------------------------------------
    /**
     * Replace all variable references in the string s according to the
     * replacements map.
     * 
     * @param s a string that may contain variable references of the form
     *        %name%.
     * @param replacements a map from variable name (sans "%") to string value.
     * @return the string with all variable references replaced by their values.
     */
    public static String replace(String s, Map<String, String> replacements) {
        for (Entry<String, String> entry : replacements.entrySet()) {
            s = s.replace('%' + entry.getKey() + '%', entry.getValue());
        }
        return s;
    }

    // ------------------------------------------------------------------------
    /**
     * Highlight all known variables in a message or command string in lime
     * green if the variable has a defined replacement, or in red if the
     * variable name is invalid and can not be replaced.
     * 
     * @param s the command or message string.
     * @param variables an array of valid variable names (without surrounding %
     *        characters).
     * @return the string with variables highlighted in lime or red.
     */
    public static String replaceDescription(String s, String[] variables) {
        String replaced = s.replaceAll("(%(?:\\w|-)+%)", "&c$1&f");
        String[] replacements = new String[variables.length * 2];
        for (int i = 0; i < variables.length; ++i) {
            replacements[2 * i] = variables[i];
            replacements[2 * i + 1] = "&a%" + variables[i] + '%';
        }
        return replace(replaced, replacements);
    }

} // class Util