## VPN CONTROL ##

Denies entry to people who use VPN, unless they are put on a whitelist.  
This plugin requires a mysql/mariadb database.

### Creating a mysql/mariadb database on linux or the windows linux subsystem
```
sudo su root

mysql -u root

CREATE DATABASE `<your_database_name_here>`;

CREATE USER '<your_username_here>'@'localhost' 
    IDENTIFIED BY '<your_database_password_here>';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, INDEX, DROP, ALTER, CREATE TEMPORARY TABLES, LOCK TABLES 
    ON `<your_database_name_here>`.* 
    TO '<your_username_here>'@'localhost';

quit
````

Enter the database name, username and password in the plugin config.

### Commands:

*/vpncontrol whitelist add <playername>* - Adds a player to the whitelist for people who are allowed to use a vpn  
*/vpncontrol whitelist remove <playername>* - Removes a player from the whitelist  
*/vpncontrol whitelist status <playername>* - Gets the status of a player on the whitelist  
*/vpncontrol refresh* - Deletes all whitelist entries in the database, loads new from github. 

### Java and mysql

It may be that there is no mysql connector installed for java on your server host.  
If this is the case when you try to load the server with the plugin installed you'll get an error like this:  
```Caused by java.lang.RuntimeException: Can't load resource bundle due to underlying exception java.util.MissingResourceException: Can't find bundle for base name com.mysql.cj.LocalizeErrorMessages, locale```  

![Example of error message](https://i.imgur.com/si4b0UW.png)  

To install it ssh into your server and execute the command `sudo apt-get install libmariadb-java`

Should this not work, giving a message *"Unable to locate package libmariadb-java"*, like on some debian buster servers, download libmaria-db from a server here: https://packages.debian.org/buster/all/libmariadb-java/download  

Upload it to your server and run `sudo dpkg -i libmaria*.deb && sudo apt-get -f install`

### Configuration

When the plugin first starts it will write a plugin file to `server_path/plugins/VpnControl/config.yaml`
```yaml
mysql-host: 127.0.0.1
mysql-port: '3306'
mysql-database: database
mysql-username: username
mysql-password: password
blocked-message: You haven't registered as a VPN user with this server. Register as
  user at https://www.example.com
notify-message: <player> has joined via VPN.
```

**mysql-host** The IP adress of your mysql server. Usually this will be localhost. But if you have a dedicated mysql server enter it's IP here. Usually you don't need to change this.  
**mysql-port** The port on which your mysql server listens to connections. Usually this is 3306.  
**mysql-database** The name of your database. This is what you entered after `CREATE TABLE` if you used the commands above for setting up a database.  
**mysql-username** The username of the user that can access your database. DO NOT USE ROOT! please create a dedicated user with `CREATE USER `. Using root is bad, m'kay?   
**mysql-password** The password for the above user. Always use a password.    
**blocked-message** The message you want to show when a user joins that uses vpn and isn't whitelisted. It's recommended to add a website/discord url where they can register.  
**notify-message** People that have the vpncontrol.notify permission will get this message when a whitelisted user using VPN joins. Use <player> where the username of the player that joined should go.  

### Permissions

There are 3 permissions in this plugin  
**vpncontrol.whitelist** Allow users with this permission to add and remove users from the vpn control whitelist  
**vpncontrol.refresh** Allow users with this permission to refresh the IP list of vpns  
**vpncontrol.notify** Users with this permission get a message when people using vpn join  
