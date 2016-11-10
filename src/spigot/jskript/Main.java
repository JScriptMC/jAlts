package spigot.jskript;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
 
public class Main extends org.bukkit.plugin.java.JavaPlugin implements org.bukkit.event.Listener
{
FileConfiguration logins;
File loginsFile;
FileConfiguration config;
File configFile;
Logger logger;
Player player;
Plugin plugin;
Server mc;
   
public void onEnable()
{
this.plugin = this;
this.mc = getServer();
this.logger = getLogger();
this.mc.getPluginManager().registerEvents(this, this);
this.config = getConfig();
this.config.options().copyDefaults(true);
this.loginsFile = new File(getDataFolder(), "LOGINS");
this.configFile = new File(getDataFolder(), "config.yml");
this.logins = YamlConfiguration.loadConfiguration(this.loginsFile);
saveConfig();
savelogins();
}
public void onDisable() {}   


public void reloadlogins()
   {
     this.logins = YamlConfiguration.loadConfiguration(this.loginsFile);
   }
   
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
   {
     boolean isPlayer = sender instanceof Player;
     if (isPlayer) sender = (Player)sender;
     if (cmd.getName().equalsIgnoreCase("jAlts")) {
       if (args.length < 1) {
         return false;
       }
       if (args[0].equalsIgnoreCase("reload")) {
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        reloadlogins();
        this.logger.info("Configurations reloaded.");
        if (isPlayer) sender.sendMessage(org.bukkit.ChatColor.GREEN + "Configurations reloaded.");
        return true;
       }
     }
     return false;
   }
   
   public void savelogins() {
     try {
       this.logins.save(this.loginsFile);
     }
     catch (IOException e) {
       e.printStackTrace();
     }
   }
   
   @EventHandler(priority=EventPriority.HIGHEST)
   public void checkIP(PlayerLoginEvent event)
   {
     Player player = event.getPlayer();
     String name = player.getName();
     String _name = name.toLowerCase();
     String ip = event.getAddress().getHostAddress();
     if (player.hasPermission("tAlts.bypass")) {
       this.logger.info("Allowing " + name + " (Has bypass permission)");
       return;
     }
     if ((this.logins.getString(_name) == null) || (this.logins.getString(_name) == "")) {
       Collection<Object> counting = this.logins.getValues(true).values();
       int count = 0;
       for (; counting.contains(ip); 
           counting.remove(ip)) { count++;
       }
       if ((count < this.config.getInt("max_accounts")) || (this.config.getInt("max_accounts") < 1)) {
         this.logins.set(_name, ip);
         savelogins();
       } else {
         event.disallow(PlayerLoginEvent.Result.KICK_OTHER, this.config.getString("kickaccount"));
         return;
       }
     }
   }
}
