package tschallacka.de.spigot.vpncontrol.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import tschallacka.de.spigot.vpncontrol.VpnControl;
import tschallacka.de.spigot.vpncontrol.sql.WhitelistPlayer;

import java.util.ArrayList;
import java.util.List;

public class WhitelistCommand implements CommandExecutor, TabCompleter {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 3 && args[0].equalsIgnoreCase("whitelist") && sender instanceof Player) {

            Player creator = (Player) sender;

            String type = args[1];
            if (type.equalsIgnoreCase("add")) {
                return this.addPlayer(args[2], creator);
            }
            if (type.equalsIgnoreCase("remove")) {
                return this.removePlayer(args[2], creator);
            }
            if (type.equalsIgnoreCase("status")) {
                return this.lookupStatus(args[2], creator);
            }
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("refresh")) {
            if (sender.hasPermission("vpncontrol.refresh")) {
                VpnControl.refreshVpnList = true;
                VpnControl.refreshVpnListCommander = sender.getName();
                sender.sendMessage("The IP list will be refreshed on next scheduled server restart or when /reload is called. Refreshing may take up to 30 seconds or more.");
                VpnControl.log().info(sender.getName() + " has scheduled a vpn ip list refresh on next scheduled server restart");
            } else {
                sender.sendMessage("You cannot do that. You lack the power");
                VpnControl.log().info(sender.getName() + " tried to refresh the the vpn ip list, but doesn't have the vpncontrol.refresh permission");
            }
            return true;
        }

        // If the player (or console) uses our command correct, we can return true
        return false;
    }

    @Override
    public List<String> onTabComplete (CommandSender sender, Command cmd, String label, String[] args){
        ArrayList<String> list = new ArrayList<String>();

        if(args.length == 1) {
            if(sender.hasPermission("vpncontrol.whitelist")) {
                list.add("whitelist");
            }
            if(sender.hasPermission("vpncontrol.refresh")) {
                list.add("refresh");
            }
            if(list.size() > 0) {
                return list;
            }
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("whitelist")) {
            if(sender.hasPermission("vpncontrol.whitelist")) {
                list.add("add");
                list.add("remove");
                list.add("status");
                return list;
            }
        }

        return null;
    }

    private boolean addPlayer(String arg, Player creator)
    {
        if(WhitelistPlayer.addToWhitelist(arg, creator)) {

            return true;
        }
        return false;
    }

    private boolean removePlayer(String arg, Player creator)
    {
        if(WhitelistPlayer.removeFromWhitelist(arg, creator)) {
            return true;
        }
        return false;
    }

    private boolean lookupStatus(String arg, Player creator)
    {
        return WhitelistPlayer.retrieveStatus(arg, creator);
    }
}
