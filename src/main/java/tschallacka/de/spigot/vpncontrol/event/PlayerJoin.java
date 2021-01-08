package tschallacka.de.spigot.vpncontrol.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import tschallacka.de.spigot.vpncontrol.VpnControl;
import tschallacka.de.spigot.vpncontrol.config.VpnConfig;
import tschallacka.de.spigot.vpncontrol.sql.IPLookup;
import tschallacka.de.spigot.vpncontrol.sql.WhitelistPlayer;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class PlayerJoin implements Listener
{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
        Player player = e.getPlayer();
        InetSocketAddress socket_address = player.getAddress();
        InetAddress address = socket_address.getAddress();
        byte[] ip4 = address.getAddress();
        StringBuilder builder = new StringBuilder();
        int c = 0;
        if(ip4.length > 4) {
            throw new RuntimeException("This plugin cannot handle ipv6 ips, \"yet\" ");
       }
        for(byte b : ip4) {
            builder.append(b & 0xFF);
            if(c < 3)
                builder.append('.');
            c++;
        }
        String ip = builder.toString();
        if(IPLookup.isVpn(ip)) {
            if(WhitelistPlayer.isWhitelisted(player)) {
                final String notifyMessage = ChatColor.translateAlternateColorCodes('&',
                        "&"+ChatColor.GOLD.getChar()
                                +VpnConfig.notifyMessage.replace("<player>", player.getName())
                );
                Bukkit.getOnlinePlayers().stream()
                        .filter((online_player) -> online_player.hasPermission("vpncontrol.notify"))
                        .forEach((online_player) -> online_player.sendMessage(notifyMessage));
            }
            else {
                player.kickPlayer(VpnConfig.blockedMessage);
            }
        }

    }
}
