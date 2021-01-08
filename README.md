## VPN CONTROL ##

Denies entry to people who use VPN, unless they are put on a whitelist.  
This plugin requires a mysql/mariadb database.

#Creating a mysql/mariadb database on linux or the windows linux subsystem
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

#Commands:

*/vpncontrol whitelist add <playername>* - Adds a player to the whitelist for people who are allowed to use a vpn  
*/vpncontrol whitelist remove <playername>* - Removes a player from the whitelist  
*/vpncontrol whitelist status <playername>* - Gets the status of a player on the whitelist  
*/vpncontrol refresh* - Deletes all whitelist entries in the database, loads new from github. 