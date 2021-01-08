package tschallacka.de.spigot.vpncontrol.sql;

import tschallacka.de.spigot.vpncontrol.VpnControl;
import tschallacka.de.spigot.vpncontrol.git.IPList;
import tschallacka.de.spigot.vpncontrol.util.network.SubnetUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Migration
{
    protected Connection connection;

    public Migration(Connection connection)
    {
        this.connection = connection;
    }

    public void checkStructure()
    {
        if(!Table.exists(Tables.IPV4, connection)) {
            this.createIpv4();
            this.createWhitelist();
        }
    }

    public void createWhitelist()
    {
        PreparedStatement statement;
        try {
            statement = this.connection.prepareStatement("CREATE TABLE " + Tables.ALLOWED_USERS + " ("
                    +    "uuid varbinary(16)  not null," +
                    "created_by varbinary(16) not null," +
                    "created_at datetime,"+
                    "primary key (uuid)" +
                      ")" +
                    "");
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst preparing SQL query for creating whitelist table", e);
        }
        try {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst executing SQL query for creating whitelist table", e);
        }
    }

    public void createIpv4()
    {
        PreparedStatement statement;
        try {
            statement = this.connection.prepareStatement("CREATE TABLE " + Tables.IPV4 + " ("
                    +    " ip INT  not null," +
                    "primary key (ip)"
                    +  ")" +
                    "");
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst preparing SQL query for creating ipv4 table", e);
        }
        try {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst executing SQL query for creating ipv4 table", e);
        }
        try {
            statement = this.connection.prepareStatement("CREATE TABLE " + Tables.IPV4_RANGE + " ("
                    +    " ip_start int not null," +
                    "ip_end int not null," +
                    "primary key (ip_start, ip_end)"
                    +  ")" +
                    "");
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst preparing SQL query for creating ipv4 range table", e);
        }
        try {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst executing SQL query for creating ipv4 range table", e);
        }
        this.seedIpv4();
    }
    public int counter = 0;
    public void seedIpv4()
    {

        try {
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException("Can't set autocommitting to false for mysql database", e);
        }
        final PreparedStatement statement_single;
        final PreparedStatement statement_range;
        try {
            statement_single = this.connection.prepareStatement("insert IGNORE  into " +
                    Tables.IPV4 +
                    "(ip) values (" +
                    "INET_ATON(?)" +
                    ")");
            statement_range = this.connection.prepareStatement("insert IGNORE  into " +
                    Tables.IPV4_RANGE +
                    "(ip_start, ip_end) values (" +
                    "INET_ATON(?), INET_ATON(?)" +
                    ")");
            IPList.loadIpv4List((str) -> {
                str = str.trim();
                if(str.isEmpty()) return;
                if(str.charAt(0) == '#') return;
                if(str.indexOf("/") != -1) {
                    SubnetUtils utils = new SubnetUtils(str);
                    String[] addresses = utils.getInfo().getAllAddresses();

                    counter++;
                    try {
                        if(counter % 1000 == 0) {
                            VpnControl.log().info("parsing progress at "+counter + "/~33000");

                            statement_range.executeBatch();
                            this.connection.commit();

                        }

                        statement_range.setString(1, addresses[0]);
                        statement_range.setString(2, addresses[addresses.length - 1]);
                        statement_range.addBatch();
                    }
                    catch(SQLException e) {

                        throw new RuntimeException("Error whilst seeding ipv4 range at ipaddress "+
                                addresses[0] + " - "+ addresses[addresses.length - 1] +" from range "+str, e);
                    }

                }
                else {
                    try {
                        counter++;
                        if(counter % 1000 == 0) {
                            VpnControl.log().info("parsing progress at "+counter + "/~33000");
                            statement_single.executeBatch();
                            this.connection.commit();
                        }
                        statement_single.setString(1, str);
                        statement_single.addBatch();
                    } catch (SQLException e) {

                        throw new RuntimeException("Error whilst seeding ipv4 at ipaddress " + str, e);
                    }
                }
            });
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst preparing SQL query for seeding ipv4", e);
        }
        try {
            statement_single.executeBatch();
            statement_range.executeBatch();
            this.connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst commiting ipv4 data to database", e);
        }

        try {
            this.connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Can't set autocommitting to true for mysql database", e);
        }
    }
}
