package tschallacka.de.spigot.vpncontrol.config;


import org.bukkit.configuration.file.FileConfiguration;
import tschallacka.de.spigot.vpncontrol.VpnControl;

public final class VpnConfig extends Config {

    private static VpnConfig instance;

    public static String host;
    public static String port;
    public static String database;
    public static String username;
    public static String password;
    public static String blockedMessage;
    public static String notifyMessage;

    private VpnConfig()
    {
        super("config.yml");
    }


    protected String loadString(String name, String default_value)
    {
        String value = getConfig().getString(name, default_value);
        getConfig().set(name, value);
        return value;
    }

    @Override
    public void load()
    {
        host = this.loadString("mysql-host", "127.0.0.1");
        port = this.loadString("mysql-port","3306");
        database = this.loadString("mysql-database", "database");
        username = this.loadString("mysql-username", "username");
        password = this.loadString("mysql-password", "password");
        blockedMessage = this.loadString("blocked-message", "You haven't registered as a VPN user with this server. " +
                "Register as user at https://www.example.com");
        notifyMessage = this.loadString("notify-message", "<player> has joined via VPN.");
    }

    public static FileConfiguration get()
    {
        return getInstance().config;
    }

    public static VpnConfig getInstance()
    {
        if (instance == null) {
            instance = new VpnConfig();
        }
        return instance;
    }

    @Override
    public void update()
    {
        return;
    }

}