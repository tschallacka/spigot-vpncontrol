package tschallacka.de.spigot.vpncontrol.sql;

import org.bukkit.Bukkit;
import tschallacka.de.spigot.vpncontrol.VpnControl;
import tschallacka.de.spigot.vpncontrol.config.VpnConfig;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection
{
    protected java.sql.Connection connection;

    public Connection()
    {

    }

    public void openConnection() throws SQLException,
            ClassNotFoundException
    {
        if (connection != null && !connection.isClosed()) {
            return;
        }
        // Class.forName("com.mysql.jdbc.Driver"); - Use this with old version of the Driver
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                        + VpnConfig.host+ ":" + VpnConfig.port + "/" + VpnConfig.database,
                VpnConfig.username, VpnConfig.password);
    }


    public java.sql.Connection getConnection() throws SQLException {
        if(connection != null && !connection.isClosed()) {
            return connection;
        }
        try {
            this.openConnection();
        } catch (ClassNotFoundException e) {
            VpnControl.log().info("Can't find mysql java jdbc drivers. This plugin cannot run without those. " +
                    "Remove this plugin or install jdbc drivers. " + e.getMessage());
            Bukkit.shutdown();
        }
        return connection;
    }

    public void closeConnection() throws SQLException {
        if(this.connection != null) {
            this.connection.close();
        }
    }
}
