package nu.nerd.nerdpoints;

import java.util.function.Supplier;

import org.bukkit.configuration.ConfigurationSection;

// ----------------------------------------------------------------------------
/**
 * A player setting value that can be persisted.
 * 
 * Settings that match the default value in the plugin configuration are not
 * persisted, to save space and time.
 */
public class PlayerSetting<T> {
    // ------------------------------------------------------------------------
    /**
     * Constructor.
     * 
     * @param key the key used to look up the setting value in storage.
     * @param defaultSupplier a Supplier<T> function that returns the default
     *        value of the setting.
     */
    public PlayerSetting(String key, Supplier<T> defaultSupplier) {
        _key = key;
        _defaultSupplier = defaultSupplier;
    }

    // ------------------------------------------------------------------------
    /**
     * Assign a new value to the setting.
     * 
     * If the new value is null, or equal to the default, store the setting as
     * null so that it does not need to be persisted.
     * 
     * @param value the new value.
     */
    public void set(T value) {
        _value = (value == null || getDefault().equals(value)) ? null : value;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the value of this setting.
     * 
     * @return the value of this setting.
     */
    public T get() {
        return isDefault() ? getDefault() : _value;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the default value of this setting.
     * 
     * @return the default value of this setting.
     */
    public T getDefault() {
        return _defaultSupplier.get();
    }

    // ------------------------------------------------------------------------
    /**
     * Return true if this setting is its default value.
     * 
     * Default-valued settings don't need to be written to persistent storage.
     * 
     * @return true if this setting is its default value.
     */
    public boolean isDefault() {
        return _value == null;
    }

    // ------------------------------------------------------------------------
    /**
     * Save this setting the YAML storage, storing nothing if the setting is the
     * default value.
     * 
     * @param section the YAML section to update.
     */
    public void save(ConfigurationSection section) {
        section.set(_key, _value);
    }

    // ------------------------------------------------------------------------
    /**
     * Load this setting from YAML storage.
     * 
     * @param section the YAML section to read.
     */
    @SuppressWarnings("unchecked")
    public void load(ConfigurationSection section) {
        set((T) section.get(_key));
    }

    // ------------------------------------------------------------------------
    /**
     * Key to store setting in YAML.
     */
    private final String _key;

    /**
     * A function that supplies the default value.
     */
    private final Supplier<T> _defaultSupplier;

    /**
     * Setting value, or null if the setting matches the plugin default.
     */
    private T _value;
} // class PlayerSetting