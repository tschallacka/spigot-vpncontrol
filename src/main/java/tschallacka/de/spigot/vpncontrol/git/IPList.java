package tschallacka.de.spigot.vpncontrol.git;

import java.util.ArrayList;

public class IPList
{
    public static void loadIpv4List(LineParser parser) {

        String file = "https://raw.githubusercontent.com/tschallacka/VPNs/master/vpn-ipv4.txt";
        Pull.parseFile(file, parser);
    }

}
