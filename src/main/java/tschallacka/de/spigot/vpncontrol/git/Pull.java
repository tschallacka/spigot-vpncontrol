package tschallacka.de.spigot.vpncontrol.git;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Pull
{

    public static ArrayList<String> file(String file) {
        URL url = null;
        //String username = "user";
        //String password = "gitpwd";
        ArrayList<String> list = new ArrayList<String>();
        try {
            url = new URL(file);
            java.net.URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");

            //String userpass = username + ":" + password;
            //String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));//needs Base64 encoder, apache.commons.codec
            //uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null)
                list.add(line);

        } catch (IOException e) {
            throw new RuntimeException("Error during downloading "+file+" from github", e);
        }
        return list;
    }

    public static void parseFile(String file, LineParser parser) {
        URL url = null;

        ArrayList<String> list = new ArrayList<String>();
        try {
            url = new URL(file);
            java.net.URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;

            while ((line = reader.readLine()) != null)
                parser.parse(line);

        } catch (IOException e) {
            throw new RuntimeException("Error during downloading "+file+" from github", e);
        }
    }
}
