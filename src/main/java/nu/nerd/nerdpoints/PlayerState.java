package nu.nerd.nerdpoints;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import nu.nerd.nerdpoints.format.Fixed1;
import nu.nerd.nerdpoints.format.Scope;
import nu.nerd.nerdpoints.format.TextSupplier;

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
    public final FormatSetting hudFormat;

    /**
     * The format string that defines the value of %biome% used in the HUD
     * layout.
     */
    public final FormatSetting biomeFormat;

    /**
     * The format string that defines the value of %chunk% used in the HUD
     * layout.
     */
    public final FormatSetting chunkFormat;

    /**
     * The format string that defines the value of %compass% used in the HUD
     * layout.
     */
    public final FormatSetting compassFormat;

    /**
     * The format string that defines the value of %coords% used in the HUD
     * layout.
     */
    public final FormatSetting coordsFormat;

    /**
     * The format string that defines the value of %light% used in the HUD
     * layout.
     */
    public final FormatSetting lightFormat;

    // ------------------------------------------------------------------------
    /**
     * Constructor.
     *
     * @param player the player.
     */
    public PlayerState(Player player) {
        _player = player;

        hudVisible = new PlayerSetting<>("hud-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_HUD_VISIBLE);
        biomeVisible = new PlayerSetting<>("biome-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_BIOME_VISIBLE);
        chunkVisible = new PlayerSetting<>("chunk-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_CHUNK_VISIBLE);
        compassVisible = new PlayerSetting<>("compass-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_COMPASS_VISIBLE);
        coordsVisible = new PlayerSetting<>("coords-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_COORDS_VISIBLE);
        lightVisible = new PlayerSetting<>("light-visible", () -> NerdPoints.CONFIG.HUD_DEFAULT_LIGHT_VISIBLE);

        hudFormat = new FormatSetting("hud-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_HUD_FORMAT);
        biomeFormat = new FormatSetting("biome-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_BIOME_FORMAT);
        chunkFormat = new FormatSetting("chunk-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_CHUNK_FORMAT);
        compassFormat = new FormatSetting("compass-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_COMPASS_FORMAT);
        coordsFormat = new FormatSetting("coords-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_COORDS_FORMAT);
        lightFormat = new FormatSetting("light-format", () -> NerdPoints.CONFIG.HUD_DEFAULT_LIGHT_FORMAT);

        load();

        // Set up the biome Scope.
        _biomeScope.set("biome",
                        new TextSupplier<Biome>(
                            () -> _block.getBiome(), b -> {
                                String biome = NerdPoints.CONFIG.HUD_BIOME_NAMES.get(b);
                                if (biome == null) {
                                    biome = b.name().toLowerCase().replace('_', ' ');
                                }
                                return biome;
                            }));

        // Set up the chunk Scope.
        _chunkScope.set("cx", new TextSupplier<Integer>(() -> _block.getX() >> 4, i -> Integer.toString(i)));
        _chunkScope.set("cy", new TextSupplier<Integer>(() -> _block.getY() >> 4, i -> Integer.toString(i)));
        _chunkScope.set("cz", new TextSupplier<Integer>(() -> _block.getZ() >> 4, i -> Integer.toString(i)));
        _chunkScope.set("x", new TextSupplier<Integer>(() -> _block.getX() & 0xF, i -> Integer.toString(i)));
        _chunkScope.set("y", new TextSupplier<Integer>(() -> _block.getY() & 0xF, i -> Integer.toString(i)));
        _chunkScope.set("z", new TextSupplier<Integer>(() -> _block.getZ() & 0xF, i -> Integer.toString(i)));

        // Set up the compass Scope.
        // Integer octant index => octant string.
        _compassScope.set("octant",
                          new TextSupplier<Integer>(
                              () -> (int) ((_location.getYaw() + 360.0f + 22.5f) / 45.0f) & 0x7,
                              i -> OCTANTS[i]));

        _compassScope.set("heading",
                          new TextSupplier<Integer>(
                              () -> Math.round(_location.getYaw() + 360) % 360,
                              i -> String.format("%3d", i)));

        _compassScope.set("heading.",
                          // Prevent 359.95 being displayed as "360.0".
                          // Fixed point with one decimal digit.
                          new TextSupplier<Fixed1>(
                              () -> new Fixed1(Math.round((_location.getYaw() + 360) * 10) % 3600),
                              f -> f.toString(5)));

        // Set up the coords Scope.
        _coordsScope.set("x", new TextSupplier<Integer>(() -> _location.getBlockX(), i -> Integer.toString(i)));
        _coordsScope.set("y", new TextSupplier<Integer>(() -> _location.getBlockY(), i -> Integer.toString(i)));
        _coordsScope.set("z", new TextSupplier<Integer>(() -> _location.getBlockZ(), i -> Integer.toString(i)));
        _coordsScope.set("x.", new TextSupplier<Fixed1>(() -> new Fixed1((float) _location.getX()), f -> f.toString()));
        _coordsScope.set("y.", new TextSupplier<Fixed1>(() -> new Fixed1((float) _location.getY()), f -> f.toString()));
        _coordsScope.set("z.", new TextSupplier<Fixed1>(() -> new Fixed1((float) _location.getZ()), f -> f.toString()));

        // Set up the light Scope.
        _lightScope.set("light",
                        new TextSupplier<Integer>(
                            // Ignore Block.getLightLevel(). Compute per F3.
                            () -> (_block != null) ? Math.max(_block.getLightFromSky(), _block.getLightFromBlocks()) : 0,
                            i -> String.format("%2d", i)));
        _lightScope.set("skylight",
                        new TextSupplier<Integer>(
                            () -> (_block != null) ? (int) _block.getLightFromSky() : 0,
                            i -> String.format("%2d", i)));
        _lightScope.set("blocklight",
                        new TextSupplier<Integer>(
                            () -> (_block != null) ? (int) _block.getLightFromBlocks() : 0,
                            i -> String.format("%2d", i)));
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

    // --------------------------------------------------------------------------
    /**
     * Return true if the player is showing their HUD (visible and not
     * suspended).
     * 
     * @return true if the player is showing their HUD (visible and not
     *         suspended).
     */
    public boolean isShowingHUD() {
        return hudVisible.get() && !isHUDSuspended();
    }

    // --------------------------------------------------------------------------
    /**
     * Synchronously to the main thread cache state that can be used to
     * asynchronously update this player's HUD in {@link #asyncUpdateHUD()}.
     */
    public void syncPrepareHUDUpdate() {
        if (isShowingHUD()) {
            _location = _player.getLocation();
            _block = _location.getBlock();
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Perform asynchronous computations of the new HUD text based on state
     * cached by {@link PlayerState#syncPrepareHUDUpdate()}.
     */
    public void asyncUpdateHUD() {
        if (!isShowingHUD()) {
            return;
        }

        _hudScope.setText("biome", biomeVisible.get() ? biomeFormat.get().expand(_biomeScope) : "");
        _hudScope.setText("chunk", chunkVisible.get() ? chunkFormat.get().expand(_chunkScope) : "");
        _hudScope.setText("compass", compassVisible.get() ? compassFormat.get().expand(_compassScope) : "");
        _hudScope.setText("coords", coordsVisible.get() ? coordsFormat.get().expand(_coordsScope) : "");
        _hudScope.setText("light", lightVisible.get() ? lightFormat.get().expand(_lightScope) : "");

        String uncoloured = hudFormat.get().expand(_hudScope);
        String message = ChatColor.translateAlternateColorCodes('&', uncoloured);
        String limited = message.substring(0, Math.min(message.length(), MAX_HUD_LENGTH));
        _player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(limited));
    }

    // ------------------------------------------------------------------------
    /**
     * Return true if all the player's settings are defaults.
     * 
     * @return true if all the player's settings are defaults.
     */
    public boolean isDefault() {
        return hudVisible.isDefault() &&
               biomeVisible.isDefault() &&
               chunkVisible.isDefault() &&
               compassVisible.isDefault() &&
               coordsVisible.isDefault() &&
               lightVisible.isDefault() &&
               hudFormat.isDefault() &&
               biomeFormat.isDefault() &&
               chunkFormat.isDefault() &&
               compassFormat.isDefault() &&
               coordsFormat.isDefault() &&
               lightFormat.isDefault();
    }

    // ------------------------------------------------------------------------
    /**
     * Save the player's preferences.
     */
    protected void save() {
        YamlConfiguration config = new YamlConfiguration();
        try {
            File file = NerdPoints.PLUGIN.getPlayerFile(_player.getUniqueId());
            if (isDefault()) {
                file.delete();
            } else {
                save(config);
                config.save(file);
            }
        } catch (IOException ex) {
            NerdPoints.PLUGIN.getLogger().warning("Unable to save player data for " + _player.getName() +
                                                  " (" + _player.getUniqueId() + "): " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Save this player's preferences to the specified configuration section.
     * 
     * @param section the configuration section.
     */
    public void save(ConfigurationSection section) {
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
     * Load this player's preferences.
     */
    public void load() {
        File file = NerdPoints.PLUGIN.getPlayerFile(_player.getUniqueId());
        if (file.canRead()) {
            load(YamlConfiguration.loadConfiguration(file));
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Load the player's preferences from the specified configuration section.
     * 
     * @param section the configuration section.
     */
    public void load(ConfigurationSection section) {
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
     * Most recent Location of Player.
     */
    protected Location _location;

    /**
     * Most recent block at _location.
     */
    protected Block _block;

    /**
     * Scope containing HUD variables.
     */
    protected Scope _hudScope = new Scope();

    /**
     * Scope containing biome variables.
     */
    protected Scope _biomeScope = new Scope();

    /**
     * Scope containing chunk variables.
     */
    protected Scope _chunkScope = new Scope();

    /**
     * Scope containing compass variables.
     */
    protected Scope _compassScope = new Scope();

    /**
     * Scope containing coords variables.
     */
    protected Scope _coordsScope = new Scope();

    /**
     * Scope containing light variables.
     */
    protected Scope _lightScope = new Scope();

    /**
     * The time at which suspendHUD() was called.
     */
    protected long _suspendTime;

} // class PlayerState