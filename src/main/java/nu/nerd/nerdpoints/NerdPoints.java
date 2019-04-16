package nu.nerd.nerdpoints;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import nu.nerd.nerdpoints.commands.ExecutorBase;
import nu.nerd.nerdpoints.commands.HudExecutor;
import nu.nerd.nerdpoints.commands.NerdPointsExecutor;

// ----------------------------------------------------------------------------
/**
 * Main plugin class.
 */
public class NerdPoints extends JavaPlugin implements Listener {
    /**
     * This plugin instance.
     */
    public static NerdPoints PLUGIN;

    /**
     * The configuration as a singleton.
     */
    public static Configuration CONFIG = new Configuration();

    // ------------------------------------------------------------------------
    /**
     * Suspend the HUD display (uses the action bar) of the specified player for
     * the configured duration (default: 3 seconds).
     * 
     * @param player the player, who must be online.
     */
    public void suspendHUD(Player player) {
        PlayerState state = getState(player);
        if (state != null) {
            state.suspendHUD();
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Return the {@link PlayerState} for the specified player.
     *
     * @param player the player.
     * @return the {@link PlayerState} for the specified player.
     */
    public PlayerState getState(Player player) {
        return _state.get(player.getName());
    }

    // ------------------------------------------------------------------------
    /**
     * Return the path to the directory containing player settings files.
     * 
     * @return the path to the directory containing player settings files.
     */
    public File getPlayersDir() {
        return new File(getDataFolder(), PLAYERS_DIR);
    }

    // ------------------------------------------------------------------------
    /**
     * Return the path to the player settings file for the player with the
     * specified UUID.
     * 
     * @param uuid the player's UUID.
     * @return the file.
     */
    public File getPlayerFile(UUID uuid) {
        return new File(getPlayersDir(), uuid.toString() + ".yml");
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     * 
     *      Load the config and player settings. Start a task to update action
     *      bars.
     */
    @Override
    public void onEnable() {
        PLUGIN = this;
        _actionBarPool = new ForkJoinPool();

        saveDefaultConfig();
        CONFIG.reload(false);
        migratePlayerSettings();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> updateActionBars(), CONFIG.HUD_UPDATE_TICKS);
        Bukkit.getPluginManager().registerEvents(this, this);

        addCommandExecutor(new HudExecutor());
        addCommandExecutor(new NerdPointsExecutor());
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onDisable()
     * 
     *      Stop all tasks and save the player settings.
     */
    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        // Actually, they should all have been kicked...
        for (PlayerState state : _state.values()) {
            state.save();
        }
        _actionBarPool.shutdown();
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * When the player interacts with a material that could require the use of
     * the Action Bar, temporarily suspend the HUD.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block block = event.getClickedBlock();
        Material material = (block != null) ? block.getType() : null;
        if (CONFIG.HUD_SUSPEND_MATERIALS.contains(material)) {
            suspendHUD(event.getPlayer());
        }
    }

    // ------------------------------------------------------------------------
    /**
     * On join, allocate each player a {@link PlayerState} instance.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        _state.put(player.getName(), new PlayerState(player));
    }

    // ------------------------------------------------------------------------
    /**
     * On quit, forget the {@link PlayerState}.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        PlayerState state = _state.remove(event.getPlayer().getName());
        state.save();
    }

    // ------------------------------------------------------------------------
    /**
     * Update the action bar displays of all players.
     */
    protected void updateActionBars() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> updateActionBars(), CONFIG.HUD_UPDATE_TICKS);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            PlayerState state = getState(player);
            state.syncPrepareHUDUpdate();
        }

        if (CONFIG.HUD_UPDATE_PARALLEL) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                PlayerState state = getState(player);
                _actionBarPool.submit(() -> state.asyncUpdateHUD());
            }
            _actionBarPool.awaitQuiescence(CONFIG.HUD_UPDATE_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } else {
            // Synchronous to main thread.
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                getState(player).asyncUpdateHUD();
            }
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Migrate player settings from PLAYERS_FILE to individual files in
     * PLAYERS_DIR.
     */
    protected void migratePlayerSettings() {
        File inputFile = new File(getDataFolder(), PLAYERS_FILE);
        if (inputFile.canRead()) {
            YamlConfiguration inputConfig = YamlConfiguration.loadConfiguration(inputFile);
            for (String uuidString : inputConfig.getKeys(false)) {
                // Read one section from old config and create an equivalent
                // section at the top level of the output file.
                YamlConfiguration playerConfig = new YamlConfiguration();
                ConfigurationSection inputSection = inputConfig.getConfigurationSection(uuidString);
                for (String key : inputSection.getKeys(false)) {
                    playerConfig.set(key, inputSection.get(key));
                }

                try {
                    playerConfig.save(new File(getPlayersDir(), uuidString + ".yml"));
                } catch (IOException ex) {
                    getLogger().warning("Unable to migrate player data for " + uuidString + ": " + ex.getMessage());
                }
            }

            // Don't migrate again.
            inputFile.renameTo(new File(getDataFolder(), PLAYERS_FILE + ".migrated"));
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Add the specified CommandExecutor and set it as its own TabCompleter.
     * 
     * @param executor the CommandExecutor.
     */
    protected void addCommandExecutor(ExecutorBase executor) {
        PluginCommand command = getCommand(executor.getName());
        command.setExecutor(executor);
        command.setTabCompleter(executor);
    }

    // ------------------------------------------------------------------------
    /**
     * Name of players directory, which stores player settings as <uuid>.yml.
     */
    public static final String PLAYERS_DIR = "players/";

    /**
     * Name of the old players file, which was used to store player settings.
     * 
     * Player settings are migrated from this file (if it exists) to individual
     * files in PLAYERS_DIR on plugin startup.
     */
    public static final String PLAYERS_FILE = "players.yml";

    /**
     * Map from Player name to {@link PlayerState} instance.
     *
     * A Player's PlayerState exists only for the duration of a login.
     */
    protected HashMap<String, PlayerState> _state = new HashMap<>();

    /**
     * ForkJoinPool used to compute the formatted text of player HUDS in
     * parallel.
     */
    protected ForkJoinPool _actionBarPool;
} // class NerdPoints