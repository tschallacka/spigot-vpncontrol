package tschallacka.de.spigot.vpncontrol.sql;

import tschallacka.de.spigot.vpncontrol.VpnControl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IPLookup
{
    public static boolean isVpn(String ip)
    {
        Connection connection;
        try {
            connection = VpnControl.connection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database, " +
                    "erring on the side of caution, but vpn control is now disabled" +
                    ", anyone can join. No vpn checks", e);
        }
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "Select ip from `" + Tables.IPV4 + "` where `ip` = INET_ATON(?)"
            );
            statement.setString(1, ip);
            ResultSet result = statement.executeQuery();
            if (result.next() != false) {
                result.close();
                return true;
            }
            result.close();
            statement = connection.prepareStatement(
                    "Select ip_start from `" + Tables.IPV4_RANGE + "` where `ip_start` <= INET_ATON(?) and ip_end >= INET_ATON(?)"
            );
            statement.setString(1, ip);
            statement.setString(2, ip);
            result = statement.executeQuery();
            if (result.next() != false) {
                result.close();
                return true;
            }
        }
        catch(SQLException e) {
            throw new RuntimeException("Trouble with querying the database for "+ip, e);
        }
        return false;
    }
}
