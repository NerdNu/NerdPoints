package nu.nerd.nerdpoints.commands;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nu.nerd.nerdpoints.NerdPoints;
import nu.nerd.nerdpoints.PlayerSetting;
import nu.nerd.nerdpoints.PlayerState;

// ----------------------------------------------------------------------------
/**
 * CommandExecutor implementation for the /hud command.
 */
public class HudExecutor extends ExecutorBase {
    // ------------------------------------------------------------------------
    /**
     * Default constructor.
     */
    public HudExecutor() {
        super("hud", "help", "on", "off", "format", "biome", "chunk", "compass", "coords", "light");
    }

    // ------------------------------------------------------------------------
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
            onHudCommand(sender, "Head Up Display",
                         ChatColor.GOLD + " Run " + ChatColor.YELLOW + "/hud help" + ChatColor.GOLD + " for help.",
                         state.hudVisible, state.hudFormat);
            return true;
        }

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("on") ||
                args[0].equalsIgnoreCase("off") ||
                args[0].equalsIgnoreCase("format")) {
                onHudCommand(sender, "Head Up Display", "",
                             state.hudVisible, state.hudFormat, args);
                return true;
            } else if (args[0].equalsIgnoreCase("biome")) {
                onHudCommand(sender, "Biome HUD section", "",
                             state.biomeVisible, state.biomeFormat,
                             Arrays.copyOfRange(args, 1, args.length));
                return true;
            } else if (args[0].equalsIgnoreCase("chunk")) {
                onHudCommand(sender, "Chunk HUD section", "",
                             state.chunkVisible, state.chunkFormat,
                             Arrays.copyOfRange(args, 1, args.length));
                return true;
            } else if (args[0].equalsIgnoreCase("compass")) {
                onHudCommand(sender, "Compass HUD section", "",
                             state.compassVisible, state.compassFormat,
                             Arrays.copyOfRange(args, 1, args.length));
                return true;
            } else if (args[0].equalsIgnoreCase("coords")) {
                onHudCommand(sender, "Coords HUD section", "",
                             state.coordsVisible, state.coordsFormat,
                             Arrays.copyOfRange(args, 1, args.length));
                return true;
            } else if (args[0].equalsIgnoreCase("light")) {
                onHudCommand(sender, "Light HUD section", "",
                             state.lightVisible, state.lightFormat,
                             Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /hud help.");
        return true;
    }

    // ------------------------------------------------------------------------
    /**
     * Handle HUD visibility and format commands.
     *
     * @param sender the CommandSender.
     * @param description a description of the affected setting, formatted into
     *        messages.
     * @param toggleSuffix a suffix appended to the HUD visibility toggle
     *        message only.
     * @param visibilitySetting the affected visibility setting.
     * @param formatSetting the affected format setting.
     * @param args command arguments after the subcommand is known (always
     *        beginning "on", "off" or "format" for valid commands).
     */
    protected void onHudCommand(CommandSender sender,
                                String description,
                                String toggleSuffix,
                                PlayerSetting<Boolean> visibilitySetting,
                                PlayerSetting<String> formatSetting,
                                String... args) {
        if (args.length == 0) {
            visibilitySetting.set(!visibilitySetting.get());
            String enabled = visibilitySetting.get() ? "enabled" : "disabled";
            sender.sendMessage(ChatColor.GOLD + description + " " + enabled + "." + toggleSuffix);
            return;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
                visibilitySetting.set(true);
                sender.sendMessage(ChatColor.GOLD + description + " enabled.");
                return;
            } else if (args[0].equalsIgnoreCase("off")) {
                visibilitySetting.set(false);
                sender.sendMessage(ChatColor.GOLD + description + " disabled.");
                return;
            } else if (args[0].equalsIgnoreCase("format")) {
                sender.sendMessage(ChatColor.GOLD + description + " format: " + formatSetting.get());
                return;
            }
        } else if (args.length > 1 && args[0].equalsIgnoreCase("format")) {
            sender.sendMessage(ChatColor.GOLD + description + " old format: " + formatSetting.get());
            formatSetting.set(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
            sender.sendMessage(ChatColor.GOLD + description + " new format: " + formatSetting.get());
            return;
        }

        sender.sendMessage(ChatColor.RED + "Invalid arguments. Try /hud help.");
    }
} // class HudExecutor