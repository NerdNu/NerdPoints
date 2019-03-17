package nu.nerd.nerdpoints.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nu.nerd.nerdpoints.NerdPoints;
import nu.nerd.nerdpoints.PlayerState;

// --------------------------------------------------------------------------
/**
 * CommandExecutor implementation for the /hud command.
 */
public class HudExecutor extends ExecutorBase {

    // --------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public HudExecutor() {
        super("hud", "help", "on", "off", "format", "biome", "compass", "coords", "light");
    }

    // --------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            return false;
        }

        if (!isInGame(sender)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerState state = NerdPoints.PLUGIN.getState(player);

        if (args.length == 0) {
            state.hudVisible.set(!state.hudVisible.get());
            String enabled = state.hudVisible.get() ? "enabled" : "disabled";
            sender.sendMessage(ChatColor.GOLD + "Head Up Display " + enabled + ". Run " +
                               ChatColor.YELLOW + "/hud help" + ChatColor.GOLD + " for help.");
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("on")) {
                state.hudVisible.set(true);
                sender.sendMessage(ChatColor.GOLD + "Head Up Display enabled.");
                return true;
            } else if (args[0].equalsIgnoreCase("off")) {
                sender.sendMessage(ChatColor.GOLD + "Head Up Display disabled.");
                state.hudVisible.set(false);
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /hud help.");
        return true;
    }
} // class HudExecutor