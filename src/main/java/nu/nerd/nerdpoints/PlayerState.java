package nu.nerd.nerdpoints;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

// ----------------------------------------------------------------------------
/**
 * Transient, per-player state, created on join and removed when the player
 * leaves.
 * 
 * For the HUD, any setting that matches the configuration default is stored as
 * null. If all the player's settings are null, nothing is persisted to the
 * players.yml file.
 */
public class PlayerState {
    // ------------------------------------------------------------------------
    /**
     * The visibility of the HUD.
     */
    public final PlayerSetting<Boolean> hudVisible;

    /**
     * The visibility of the biome section of the HUD.
     */
    public final PlayerSetting<Boolean> biomeVisible;

    /**
     * The visibility of the chunk section of the HUD.
     */
    public final PlayerSetting<Boolean> chunkVisible;

    /**
     * The visibility of the compass section of the HUD.
     */
    public final PlayerSetting<Boolean> compassVisible;

    /**
     * The visibility of the coords section of the HUD.
     */
    public final PlayerSetting<Boolean> coordsVisible;

    /**
     * The visibility of the light section of the HUD.
     */
    public final PlayerSetting<Boolean> lightVisible;

    /**
     * The format string used by this player to lay out their HUD.
     */
    public final PlayerSetting<String> hudFormat;

    /**
     * The format string that defines the value of %biome% used in the HUD
     * layout.
     */
    public final PlayerSetting<String> biomeFormat;

    /**
     * The format string that defines the value of %chunk% used in the HUD
     * layout.
     */
    public final PlayerSetting<String> chunkFormat;

    /**
     * The format string that defines the value of %compass% used in the HUD
     * layout.
     */
    public final PlayerSetting<String> compassFormat;

    /**
     * The format string that defines the value of %coords% used in the HUD
     * layout.
     */
    public final PlayerSetting<String> coordsFormat;

    /**
     * The format string that defines the value of %light% used in the HUD
     * layout.
     */
    public final PlayerSetting<String> lightFormat;

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param player the player.
     * @param config the configuration from which player preferences are loaded.
     */
    public PlayerState(Player player, YamlConfiguration config) {
        _player = player;

        hudVisible = new PlayerSetting<>("hud-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_HUD_VISIBLE);
        biomeVisible = new PlayerSetting<>("biome-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_BIOME_VISIBLE);
        chunkVisible = new PlayerSetting<>("chunk-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_CHUNK_VISIBLE);
        compassVisible = new PlayerSetting<>("compass-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_COMPASS_VISIBLE);
        coordsVisible = new PlayerSetting<>("coords-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_COORDS_VISIBLE);
        lightVisible = new PlayerSetting<>("light-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_LIGHT_VISIBLE);

        hudFormat = new PlayerSetting<>("hud-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_HUD_FORMAT);
        biomeFormat = new PlayerSetting<>("biome-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_BIOME_FORMAT);
        chunkFormat = new PlayerSetting<>("chunk-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_CHUNK_FORMAT);
        compassFormat = new PlayerSetting<>("compass-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_COMPASS_FORMAT);
        coordsFormat = new PlayerSetting<>("coords-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_COORDS_FORMAT);
        lightFormat = new PlayerSetting<>("light-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_LIGHT_FORMAT);
        load(config);
    }

    // ------------------------------------------------------------------------
    /**
     * Perform tick task actions related to the player, such as updating the
     * player's HUD.
     */
    public void onTick() {
        updateHUD();
    }

    // --------------------------------------------------------------------------
    /**
     * Temporarily hide the HUD while something else uses the Action Bar.
     * 
     * Typically this is called because the player clicked on a bed or a jukebox
     * and vanilla minecraft <i>might</i> need to display a message.
     */
    public void suspendHUD() {
        _suspendTime = System.currentTimeMillis();
    }

    // ------------------------------------------------------------------------
    /**
     * Return true if this player's HUD is temporarily suspended.
     * 
     * @return true if this player's HUD is temporarily suspended.
     */
    public boolean isHUDSuspended() {
        long now = System.currentTimeMillis();
        // Cope with system clock change.
        return Math.abs(now - _suspendTime) < NerdPoints.CONFIG.HUD_SUSPEND_MILLIS;
    }

    // ------------------------------------------------------------------------
    /**
     * Return true if the player has any non-default settings.
     * 
     * @return true if the player has any non-default settings.
     */
    public boolean isNotDefault() {
        return !hudVisible.isDefault() ||
               !biomeVisible.isDefault() ||
               !chunkVisible.isDefault() ||
               !compassVisible.isDefault() ||
               !coordsVisible.isDefault() ||
               !lightVisible.isDefault() ||
               !hudFormat.isDefault() ||
               !biomeFormat.isDefault() ||
               !chunkFormat.isDefault() ||
               !compassFormat.isDefault() ||
               !coordsFormat.isDefault() ||
               !lightFormat.isDefault();
    }

    // ------------------------------------------------------------------------
    /**
     * Save this player's preferences to the specified configuration.
     * 
     * This method will not be called if {@link #isNotDefault()}.
     * 
     * @param config the configuration to update.
     */
    public void save(YamlConfiguration config) {
        ConfigurationSection section = config.createSection(_player.getUniqueId().toString());
        section.set("name", _player.getName());

        hudVisible.save(section);
        biomeVisible.save(section);
        chunkVisible.save(section);
        compassVisible.save(section);
        coordsVisible.save(section);
        lightVisible.save(section);

        hudFormat.save(section);
        biomeFormat.save(section);
        chunkFormat.save(section);
        compassFormat.save(section);
        coordsFormat.save(section);
        lightFormat.save(section);
    }

    // ------------------------------------------------------------------------
    /**
     * Load the Player's preferences from the specified configuration.
     * 
     * @param config the configuration from which player preferences are loaded.
     */
    public void load(YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(_player.getUniqueId().toString());
        if (section == null) {
            return;
        }

        hudVisible.load(section);
        biomeVisible.load(section);
        chunkVisible.load(section);
        compassVisible.load(section);
        coordsVisible.load(section);
        lightVisible.load(section);

        hudFormat.load(section);
        biomeFormat.load(section);
        chunkFormat.load(section);
        compassFormat.load(section);
        coordsFormat.load(section);
        lightFormat.load(section);
    }

    // ------------------------------------------------------------------------
    /**
     * Update the player's HUD.
     */
    protected void updateHUD() {
        if (!hudVisible.get() || isHUDSuspended()) {
            return;
        }

        Location loc = _player.getLocation();
        Block block = loc.getBlock();

        HashMap<String, String> variables = new HashMap<>();
        variables.put("biome", biomeVisible.get() ? formatBiome(block.getBiome()) : "");
        variables.put("chunk", chunkVisible.get() ? formatChunk(loc) : "");
        variables.put("compass", compassVisible.get() ? formatCompass(loc.getYaw()) : "");
        variables.put("coords", coordsVisible.get() ? formatCoords(loc) : "");
        variables.put("light", lightVisible.get() ? formatLight(block) : "");

        String uncoloured = Util.replace(hudFormat.get(), variables);
        String message = ChatColor.translateAlternateColorCodes('&', uncoloured);
        String limited = message.substring(0, Math.min(message.length(), MAX_HUD_LENGTH));
        _player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                     TextComponent.fromLegacyText(limited));
    }

    // ------------------------------------------------------------------------
    /**
     * Return the formatted value of %biome%, without replacing colours.
     * 
     * @param biome the biome whose name will be returned.
     * @return the name of a biome as displayed to the player.
     */
    protected static String formatBiome(Biome biome) {
        String name = NerdPoints.CONFIG.HUD_BIOME_NAMES.get(biome);
        if (name == null) {
            name = biome.name().toLowerCase().replace('_', ' ');
        }
        return name;
    }

    // ------------------------------------------------------------------------
    /**
     * Return the formatted value of %coords%, without replacing colours.
     * 
     * @param loc the player's current location.
     * @return the formatted value of %coords%, without replacing colours.
     */
    protected String formatChunk(Location loc) {
        HashMap<String, String> variables = new HashMap<>();
        int cx = loc.getBlockX() / 16;
        int cy = loc.getBlockY() / 16;
        int cz = loc.getBlockZ() / 16;
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        variables.put("cx", String.format("%d", cx));
        variables.put("cy", String.format("%d", cy));
        variables.put("cz", String.format("%d", cz));
        variables.put("x", String.format("%d", x - cx));
        variables.put("y", String.format("%d", y - cy));
        variables.put("z", String.format("%d", z - cz));
        return Util.replace(coordsFormat.get(), variables);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the formatted value of %compass%, without replacing colours.
     *
     * @param yaw the yaw angle of the player's location (S = 0 or 360, W = 90,
     *        N = 180, E = 270).
     * @return the formatted value of %compass%, without replacing colours.
     */
    protected String formatCompass(float yaw) {
        yaw += 360;
        HashMap<String, String> variables = new HashMap<>();
        // octant 0: -22.5 - 22.5, 1: 22.5 - 77.5 etc
        int octantIndex = (int) ((yaw + 22.5f) / 45.0f) % 8;
        variables.put("octant", OCTANTS[octantIndex]);
        variables.put("heading", String.format("%3d", Math.round(yaw) % 360));
        // Prevent 359.95 being displayed as "360.0".
        variables.put("heading.", String.format("%5.1f", (Math.round(yaw * 10) % 3600) / 10.0f));
        return Util.replace(compassFormat.get(), variables);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the formatted value of %coords%, without replacing colours.
     * 
     * @param loc the player's current location.
     * @return the formatted value of %coords%, without replacing colours.
     */
    protected String formatCoords(Location loc) {
        HashMap<String, String> variables = new HashMap<>();
        variables.put("x", String.format("%d", loc.getBlockX()));
        variables.put("y", String.format("%d", loc.getBlockY()));
        variables.put("z", String.format("%d", loc.getBlockZ()));
        variables.put("x.", String.format("%.1f", loc.getX()));
        variables.put("y.", String.format("%.1f", loc.getY()));
        variables.put("z.", String.format("%.1f", loc.getZ()));
        return Util.replace(coordsFormat.get(), variables);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the formatted value of %light%, without replacing colours.
     * 
     * @param block the block at the player's current location.
     * @return the formatted value of %light%, without replacing colours.
     */
    protected String formatLight(Block block) {
        HashMap<String, String> variables = new HashMap<>();
        if (block == null) {
            variables.put("light", "0");
            variables.put("skylight", "0");
            variables.put("blocklight", "0");
        } else {
            variables.put("light", String.format("%2d", block.getLightLevel()));
            variables.put("skylight", String.format("%2d", block.getLightFromSky()));
            variables.put("blocklight", String.format("%2d", block.getLightFromBlocks()));
        }
        return Util.replace(lightFormat.get(), variables);
    }

    // ------------------------------------------------------------------------
    /**
     * Maximum length of fully formatted HUD text, just in case the client is
     * sensitive to that.
     */
    static final int MAX_HUD_LENGTH = 128;

    /**
     * The 8 compass directions shown when formatting %octant%.
     */
    static final String[] OCTANTS = { " S", "SW", " W", "NW", " N", "NE", " E", "SE" };

    /**
     * The Player.
     */
    protected Player _player;

    /**
     * The time at which suspendHUD() was called.
     */
    protected long _suspendTime;

} // class PlayerState