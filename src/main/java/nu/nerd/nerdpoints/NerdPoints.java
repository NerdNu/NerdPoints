package nu.nerd.nerdpoints;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
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
     * the configured duration (default: 5 seconds).
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
     * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
     * 
     *      Load the config and player settings. Start a task to update action
     *      bars.
     */
    @Override
    public void onEnable() {
        PLUGIN = this;

        saveDefaultConfig();
        CONFIG.reload(false);

        File playersFile = new File(getDataFolder(), PLAYERS_FILE);
        _playerConfig = YamlConfiguration.loadConfiguration(playersFile);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> updateActionBars(), 1, 1);
        Bukkit.getPluginManager().registerEvents(this, this);

        addCommandExecutor(new HudExecutor());
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

        for (PlayerState state : _state.values()) {
            if (state.isNotDefault()) {
                state.save(_playerConfig);
            }
        }
        try {
            _playerConfig.save(new File(getDataFolder(), PLAYERS_FILE));
        } catch (IOException ex) {
            getLogger().warning("Unable to save player data: " + ex.getMessage());
        }
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
        _state.put(player.getName(), new PlayerState(player, _playerConfig));
    }

    // ------------------------------------------------------------------------
    /**
     * On quit, forget the {@link PlayerState}.
     */
    @EventHandler(ignoreCancelled = true)
    protected void onPlayerQuit(PlayerQuitEvent event) {
        PlayerState state = _state.remove(event.getPlayer().getName());
        if (state.isNotDefault()) {
            state.save(_playerConfig);
        }
    }

    // ------------------------------------------------------------------------
    /**
     * Update the action bar displays of all players.
     */
    protected void updateActionBars() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            getState(player).onTick();
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
     * Name of players file, which stores player settings.
     */
    protected static final String PLAYERS_FILE = "players.yml";

    /**
     * Configuration file for per-player settings, loaded on plugin enable,
     * saved on disable.
     */
    protected YamlConfiguration _playerConfig;

    /**
     * Map from Player name to {@link PlayerState} instance.
     *
     * A Player's PlayerState exists only for the duration of a login.
     */
    protected HashMap<String, PlayerState> _state = new HashMap<>();

} // class NerdPoints