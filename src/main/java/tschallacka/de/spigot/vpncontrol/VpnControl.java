package tschallacka.de.spigot.vpncontrol;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import tschallacka.de.spigot.vpncontrol.command.WhitelistCommand;
import tschallacka.de.spigot.vpncontrol.config.VpnConfig;
import tschallacka.de.spigot.vpncontrol.event.PlayerJoin;
import tschallacka.de.spigot.vpncontrol.git.IPList;
import tschallacka.de.spigot.vpncontrol.sql.Connection;
import tschallacka.de.spigot.vpncontrol.sql.Migration;
import tschallacka.de.spigot.vpncontrol.sql.Tables;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class VpnControl extends JavaPlugin {

    public static VpnControl plugin;
    public static Connection connection;
    public static boolean refreshVpnList = false;
    public static String refreshVpnListCommander = null;

    @Override
    public void onEnable() {

        plugin = this;
        VpnConfig.getInstance().setup(plugin);
        connection = new Connection();
        try {
            connection.openConnection();
        } catch (SQLException e) {
            getLogger().info("PATAL ERROR! lease insert valid mysql connection parameters in config.yml");
            Bukkit.shutdown();
        } catch (ClassNotFoundException e) {
            getLogger().info("Can't find mysql java jdbc drivers. This plugin cannot run without those. " +
                    "Remove this plugin or install jdbc drivers. " + e.getMessage());
            Bukkit.shutdown();
        }
        try {
            Migration migration = new Migration(connection.getConnection());
            migration.checkStructure();
        }
        catch(SQLException e) {
            throw new RuntimeException("Error whilst migrating tables", e);
        }
        getServer().getPluginManager().registerEvents(new PlayerJoin(), plugin);
        WhitelistCommand command = new WhitelistCommand();

        this.getCommand("vpncontrol").setExecutor(command);
        this.getCommand("vpncontrol").setTabCompleter(command);
    }

    @Override
    public void onDisable() {
        if(refreshVpnList) {
            Statement statement = null;
            try {
                log().info("Refresh of IPV4 vpn lists has been commanded by " + refreshVpnListCommander + ". Truncating existing lists");
                statement = connection.getConnection().createStatement();
                statement.executeUpdate("TRUNCATE TABLE " + Tables.IPV4);
                statement = connection.getConnection().createStatement();
                statement.executeUpdate("TRUNCATE TABLE " + Tables.IPV4_RANGE);
                log().info("All existing VPN data has been removed from the database.");
                log().info("Starting loading of fresh vpn IP data. Please wait until this process is complete");
                Migration migration = new Migration(connection.getConnection());
                migration.seedIpv4();
                log().info("Refreshing of data is complete");
            } catch (SQLException e) {
                throw new RuntimeException("An error occured during the refreshing of VPN ip data", e);
            }
        }
        try {
            connection.closeConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Could not close mysql connection!", e);
        }
    }

    public static Logger log()
    {

        return plugin.getLogger();
    }
}
