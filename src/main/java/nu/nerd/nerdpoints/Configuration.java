package nu.nerd.nerdpoints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import nu.nerd.nerdpoints.format.Format;

// ----------------------------------------------------------------------------
/**
 * Manages configuration settings.
 *
 * Several settings relate to format strings. In this plugin, format strings
 * contain variables &c colour codes and variables of the form %name%.
 */
public class Configuration {
    // ------------------------------------------------------------------------
    /**
     * Update period of the HUD in ticks.
     */
    public int HUD_UPDATE_TICKS;

    /**
     * If true, update player HUDS in parallel, off the main thread as much as
     * possible.
     */
    public boolean HUD_UPDATE_PARALLEL;

    /**
     * When updating in parallel, give up if taking longer than this number of
     * milliseconds.
     */
    public int HUD_UPDATE_TIMEOUT_MS;

    /**
     * Default HUD visibility.
     */
    public boolean HUD_DEFAULT_HUD_VISIBLE;

    /**
     * Default biome visibility within the HUD.
     */
    public boolean HUD_DEFAULT_BIOME_VISIBLE;

    /**
     * Default chunk visibility within the HUD.
     */
    public boolean HUD_DEFAULT_CHUNK_VISIBLE;

    /**
     * Default compass visibility within the HUD.
     */
    public boolean HUD_DEFAULT_COMPASS_VISIBLE;

    /**
     * Default coords visibility within the HUD.
     */
    public boolean HUD_DEFAULT_COORDS_VISIBLE;

    /**
     * Default light visibility within the HUD.
     */
    public boolean HUD_DEFAULT_LIGHT_VISIBLE;

    /**
     * Default HUD format, with &X colour codes and %var% variables.
     */
    public Format HUD_DEFAULT_HUD_FORMAT;

    /**
     * Default format of %biome%.
     */
    public Format HUD_DEFAULT_BIOME_FORMAT;

    /**
     * Default format of %chunk%.
     */
    public Format HUD_DEFAULT_CHUNK_FORMAT;

    /**
     * Default format of %compass%.
     */
    public Format HUD_DEFAULT_COMPASS_FORMAT;

    /**
     * Default format of %coords%.
     */
    public Format HUD_DEFAULT_COORDS_FORMAT;

    /**
     * Default format of %light%.
     */
    public Format HUD_DEFAULT_LIGHT_FORMAT;

    /**
     * Overridden biome names.
     * 
     * By default, to present a biome name to a player, the Biome enum is
     * converted to lower case and underscores replaced with spaces. This map
     * overrides that default for specific biomes.
     */
    public HashMap<Biome, String> HUD_BIOME_NAMES = new HashMap<>();

    /**
     * Time in milliseconds to suspend HUD updates when the player interacts
     * with HUD_SUSPEND_MATERIALS.
     */
    public int HUD_SUSPEND_MILLIS;

    /**
     * The set of materials that temporarily suspend HUD updates when the player
     * interacts with them.
     * 
     * This should be the set of blocks that cause a message to be displayed on
     * the Action Bar when clicked, e.g. beds (when you can't sleep).
     */
    public HashSet<Material> HUD_SUSPEND_MATERIALS = new HashSet<>();

    // ------------------------------------------------------------------------
    /**
     * Reload the configuration.
     * 
     * @param logged if true, configuration settings are logged to the console.
     */
    public void reload(boolean logged) {
        // NB: Changes the FileConfiguration returned by Plugin.getConfig().
        NerdPoints.PLUGIN.reloadConfig();

        FileConfiguration config = NerdPoints.PLUGIN.getConfig();
        Logger logger = NerdPoints.PLUGIN.getLogger();

        HUD_UPDATE_TICKS = config.getInt("hud.update.ticks");
        HUD_UPDATE_PARALLEL = config.getBoolean("hud.update.parallel");
        HUD_UPDATE_TIMEOUT_MS = config.getInt("hud.update.timeout-ms");

        HUD_DEFAULT_HUD_VISIBLE = config.getBoolean("hud.default.hud-visible");
        HUD_DEFAULT_BIOME_VISIBLE = config.getBoolean("hud.default.biome-visible");
        HUD_DEFAULT_CHUNK_VISIBLE = config.getBoolean("hud.default.chunk-visible");
        HUD_DEFAULT_COMPASS_VISIBLE = config.getBoolean("hud.default.compass-visible");
        HUD_DEFAULT_COORDS_VISIBLE = config.getBoolean("hud.default.coords-visible");
        HUD_DEFAULT_LIGHT_VISIBLE = config.getBoolean("hud.default.light-visible");

        HUD_DEFAULT_HUD_FORMAT = new Format(config.getString("hud.default.hud-format"));
        HUD_DEFAULT_BIOME_FORMAT = new Format(config.getString("hud.default.biome-format"));
        HUD_DEFAULT_CHUNK_FORMAT = new Format(config.getString("hud.default.chunk-format"));
        HUD_DEFAULT_COMPASS_FORMAT = new Format(config.getString("hud.default.compass-format"));
        HUD_DEFAULT_COORDS_FORMAT = new Format(config.getString("hud.default.coords-format"));
        HUD_DEFAULT_LIGHT_FORMAT = new Format(config.getString("hud.default.light-format"));

        HUD_BIOME_NAMES.clear();
        ConfigurationSection biomeNames = config.getConfigurationSection("hud.biome-names");
        for (String key : biomeNames.getKeys(false)) {
            try {
                Biome biome = Biome.valueOf(key.toUpperCase());
                HUD_BIOME_NAMES.put(biome, biomeNames.getString(key));
            } catch (IllegalArgumentException ex) {
                logger.info("Invalid biome name: " + key);
            }
        }

        HUD_SUSPEND_MILLIS = config.getInt("hud.suspend.millis");
        HUD_SUSPEND_MATERIALS.clear();
        for (String materialName : config.getStringList("hud.suspend.materials")) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                HUD_SUSPEND_MATERIALS.add(material);
            } catch (IllegalArgumentException ex) {
                logger.info("Invalid HUD suspend material name: " + materialName);
            }
        }

        if (logged) {
            logger.info("Configuration:");
            logger.info("HUD_UPDATE_TICKS: " + HUD_UPDATE_TICKS);
            logger.info("HUD_UPDATE_PARALLEL: " + HUD_UPDATE_PARALLEL);
            logger.info("HUD_UPDATE_TIMEOUT_MS: " + HUD_UPDATE_TIMEOUT_MS);
            logger.info("HUD_DEFAULT_HUD_VISIBLE: " + HUD_DEFAULT_HUD_VISIBLE);
            logger.info("HUD_DEFAULT_BIOME_VISIBLE: " + HUD_DEFAULT_BIOME_VISIBLE);
            logger.info("HUD_DEFAULT_CHUNK_VISIBLE: " + HUD_DEFAULT_CHUNK_VISIBLE);
            logger.info("HUD_DEFAULT_COMPASS_VISIBLE: " + HUD_DEFAULT_COMPASS_VISIBLE);
            logger.info("HUD_DEFAULT_COORDS_VISIBLE: " + HUD_DEFAULT_COORDS_VISIBLE);
            logger.info("HUD_DEFAULT_LIGHT_VISIBLE: " + HUD_DEFAULT_LIGHT_VISIBLE);

            logger.info("HUD_DEFAULT_HUD_FORMAT: " + HUD_DEFAULT_HUD_FORMAT);
            logger.info("HUD_DEFAULT_BIOME_FORMAT: " + HUD_DEFAULT_BIOME_FORMAT);
            logger.info("HUD_DEFAULT_CHUNK_FORMAT: " + HUD_DEFAULT_CHUNK_FORMAT);
            logger.info("HUD_DEFAULT_COMPASS_FORMAT: " + HUD_DEFAULT_COMPASS_FORMAT);
            logger.info("HUD_DEFAULT_COORDS_FORMAT: " + HUD_DEFAULT_COORDS_FORMAT);
            logger.info("HUD_DEFAULT_LIGHT_FORMAT: " + HUD_DEFAULT_LIGHT_FORMAT);

            logger.info("Biome names:");
            for (Entry<Biome, String> entry : HUD_BIOME_NAMES.entrySet()) {
                logger.info(entry.getKey().name() + " -> " + entry.getValue());
            }
            logger.info("HUD suspend millis: " + HUD_SUSPEND_MILLIS);
            logger.info("HUD suspend materials:" +
                        HUD_SUSPEND_MATERIALS.stream()
                        .map(Material::toString).collect(Collectors.joining(" ")));
        }
    } // reload
} // class Configuration