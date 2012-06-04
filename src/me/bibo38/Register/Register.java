package me.bibo38.Register;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Register extends JavaPlugin
{
	private Logger log;
	private PluginDescriptionFile pdFile;
	private static FileConfiguration cfg;
	
	private InteractListener myInteractListener;
	
	public void onEnable()
	{
		log = this.getLogger();
		pdFile = this.getDescription();
		cfg = this.getConfig();
		cfg.options().copyDefaults(true);
		this.saveConfig();
		
		File updateord = this.getServer().getUpdateFolderFile();
		if(updateord.exists() && updateord.listFiles().length == 0)
		{
			updateord.delete();
		}
		
		myInteractListener = new InteractListener();
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(myInteractListener, this);
		
		log.info("Register Version " + pdFile.getVersion() + " wurde aktiviert!");
		log.info(cfg.getString("password"));
	}
	
	public void onDisable()
	{
		log.info("Register Version " + pdFile.getVersion() + " wurde deaktiviert!");
	}
	
	public static FileConfiguration getCfg()
	{
		return cfg;
	}
	
	public static boolean hasPerm(String player)
	{
		List<String> user = cfg.getStringList("registered");
		if(user.contains(player))
		{
			return true;
		}
		return false;
	}
	
	public boolean onCommand(CommandSender cs, Command cmd, String commandLabel, String[] args)
	{
		if(!(cs instanceof Player))
		{
			cs.sendMessage(ChatColor.RED + "You can only run this Command as a Player!");
			return true;
		}
		
		Player player = (Player) cs;
		
		if(cmd.getName().equalsIgnoreCase("register") && !hasPerm(player.getName()))
		{
			if(args.length == 0 || !args[0].equals(cfg.getString("password")))
			{
				player.sendMessage(ChatColor.RED + "Incorrect Password!");
			} else
			{
				String command = cfg.getString("command1") + " " + player.getName() + " " + cfg.getString("command2");
				this.getServer().dispatchCommand(this.getServer().getConsoleSender(), command);
				player.sendMessage("Succesful Registered!");
				player.setHealth(player.getMaxHealth());
				player.setFoodLevel(20); // Voller Hunger
				
				List<String> user = cfg.getStringList("registered");
				user.add(player.getName());
				cfg.set("registered", user);
				this.saveConfig();
				this.reloadConfig();
				
				log.info(player.getName() + " was successful registered!");
			}
		}
		
		return true;
	}
	
	
}
