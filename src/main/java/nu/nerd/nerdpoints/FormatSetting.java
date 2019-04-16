package nu.nerd.nerdpoints;

import java.util.function.Supplier;

import org.bukkit.configuration.ConfigurationSection;

import nu.nerd.nerdpoints.format.Format;

// ----------------------------------------------------------------------------
/**
 * Specialisation of PlayerSetting<Format> to correctly handle persistence of
 * {@link Format}s as strings.
 */
public class FormatSetting extends PlayerSetting<Format> {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param key the key used to look up the setting value in storage.
     * @param defaultSupplier a Supplier<T> function that returns the default
     *        value of the setting.
     */
    public FormatSetting(String key, Supplier<Format> defaultSupplier) {
        super(key, defaultSupplier);
    }

    // ------------------------------------------------------------------------
    /**
     * @see PlayerSetting#save(ConfigurationSection)
     */
    @Override
    public void save(ConfigurationSection section) {
        section.set(_key, (_value != null) ? _value.toString() : null);
    }

    // ------------------------------------------------------------------------
    /**
     * @see PlayerSetting#load(ConfigurationSection)
     */
    @Override
    public void load(ConfigurationSection section) {
        String value = section.getString(_key);
        set(value != null ? new Format(value) : null);
    }
} // class FormatSetting