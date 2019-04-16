package nu.nerd.nerdpoints.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import nu.nerd.nerdpoints.NerdPoints;

// ----------------------------------------------------------------------------
/**
 * CommandExecutor implementation for the /nerdpoints command.
 */
public class NerdPointsExecutor extends ExecutorBase {
    // ------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public NerdPointsExecutor() {
        super("nerdpoints", "help", "reload");
    }

    // ------------------------------------------------------------------------
    /**
     * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender,
     *      org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            return false;
        }

        NerdPoints.CONFIG.reload(true);
        sender.sendMessage(ChatColor.GOLD + NerdPoints.PLUGIN.getName() + " configuration reloaded.");
        return true;
    }
} // class NerdPointsExecutor