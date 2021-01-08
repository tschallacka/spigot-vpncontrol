package tschallacka.de.spigot.vpncontrol.sql;

import org.bukkit.Bukkit;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tschallacka.de.spigot.vpncontrol.VpnControl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class WhitelistPlayer
{
    public static byte[] getPlayerUuid(Player player)
    {
        UUID uuid = player.getUniqueId();
        return uuidToByte(uuid);
    }

    public static byte[] uuidToByte(UUID uuid)
    {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }

    public static boolean removeFromWhitelist(String playername, Player creator)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        byte[] uuid = uuidToByte(player.getUniqueId());
        if(!isWhitelisted(uuid)) {
            creator.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&"+ChatColor.RED.getChar() + playername + " is not on the whitelist!"
                    )
            );
            return true;
        }
        if(removePlayer(uuid) ) {
            VpnControl.log().info(creator.getName() +  " removed "+playername+" from the vpncontrol whitelist");
            creator.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&"+ChatColor.GOLD.getChar() + playername + " has been removed from the whitelist"
                    )
            );
        }
        return true;
    }

    public static UUID getUUIDFromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        Long high = byteBuffer.getLong();
        Long low = byteBuffer.getLong();

        return new UUID(high, low);
    }
    public static boolean addToWhitelist(String playername, Player creator)
    {
        OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        byte[] uuid = uuidToByte(player.getUniqueId());
        if(isWhitelisted(uuid)) {
            creator.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                            "&"+ChatColor.GOLD.getChar() + playername + " is already on the whitelist!"
                    )
            );
            return true;
        }
        byte[] creator_uuid = getPlayerUuid(creator);

        try {
            Connection connection = VpnControl.connection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO`" + Tables.ALLOWED_USERS + "` (uuid, created_by, created_at) values (?, ?, NOW())"
            );
            statement.setBytes(1, uuid);
            statement.setBytes(2, creator_uuid);
            statement.executeUpdate();

        } catch (SQLException e) {

            creator.sendMessage(
                    ChatColor.translateAlternateColorCodes('&',
                        "&"+ChatColor.RED.getChar() + "An error occurred during adding entry to vpn whitelist: "
                            +e.getMessage()
                    )
            );
        }
        VpnControl.log().info(creator.getName() +  " added "+playername+" from the vpncontrol whitelist");
        creator.sendMessage(
                ChatColor.translateAlternateColorCodes('&',
                        "&"+ChatColor.GOLD.getChar() + playername + "has been added to the VPN users whitelist"
                )
        );
        return true;
    }

    public static boolean isWhitelisted(byte[] uuidBytes)
    {
        try {
            Connection connection = VpnControl.connection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "Select uuid from `" + Tables.ALLOWED_USERS + "` where `uuid` = binary ?"
            );
            statement.setBytes(1, uuidBytes);
            ResultSet result = statement.executeQuery();
            if (result.next() != false) {
                result.close();
                return true;
            };
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst looking up whitelist entry", e);
        }

    }

    public static boolean isWhitelisted(Player player)
    {
        byte[] uuidBytes = getPlayerUuid(player);
        return isWhitelisted(uuidBytes);
    }

    public static boolean removePlayer(Player player)
    {
        byte[] uuidBytes = getPlayerUuid(player);
        return removePlayer(uuidBytes);
    }

    public static boolean removePlayer(byte[] uuid)
    {
        if(isWhitelisted(uuid)) {
            try {
                Connection connection = VpnControl.connection.getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "delete from `" + Tables.ALLOWED_USERS + "` where `uuid` = binary ?"
                );
                statement.setBytes(1, uuid);
                statement.executeUpdate();
                return true;
            } catch (SQLException e) {
                throw new RuntimeException("Error whilst trying to delete player whitelist entry", e);
            }
        }
        return false;
    }

    public static void registerBlock(Player player)
    {

    }

    public static void registerLogin(Player player)
    {

    }
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
    public static boolean retrieveStatus(String playername, Player creator)
    {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
            byte[] uuid = uuidToByte(player.getUniqueId());
            VpnControl.log().info(bytesToHex(uuid));

            Connection connection = VpnControl.connection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "Select uuid, created_by, created_at from `" + Tables.ALLOWED_USERS + "` where `uuid` = binary ?"
            );
            statement.setBytes(1, uuid);
            ResultSet result = statement.executeQuery();
            if (result.next() != false) {
                byte[] created_by = result.getBytes("created_by");
                UUID created_by_uuid = getUUIDFromBytes(created_by);

                VpnControl.log().info(created_by_uuid.toString());
                OfflinePlayer created_by_player = Bukkit.getOfflinePlayer(created_by_uuid);
                java.sql.Timestamp timestamp = result.getTimestamp("created_at");
                String created =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(timestamp);
                creator.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',
                                "&"+ChatColor.GOLD.getChar() +playername + " has been whitelisted by "
                                        + created_by_player.getName()
                                        + " on " + created
                        )
                );
                result.close();

            }
            else {
                creator.sendMessage(
                        ChatColor.translateAlternateColorCodes('&',
                                "&"+ChatColor.GOLD.getChar() +playername + " is not whitelisted."
                        )
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error whilst looking up whitelist entry", e);
        }
        return true;
    }
}
