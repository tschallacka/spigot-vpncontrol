package tschallacka.de.spigot.vpncontrol.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Table {
    public static boolean exists(String tablename, Connection connection)
    {
        DatabaseMetaData dbm = null;
        try {
            dbm = connection.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst trying get tables meta data",e);
        }
// check if "employee" table is there
        ResultSet tables = null;
        try {
            tables = dbm.getTables(null, null, tablename, null);
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst trying to verify if table " +
                    tablename+" exists in mysql database",e);
        }
        try {
            if (tables.next()) {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst trying to verify if table " +
                    tablename+" exists in mysql database",e);

        }
        return false;
    }


}
