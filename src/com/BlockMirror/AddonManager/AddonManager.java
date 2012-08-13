package com.BlockMirror.AddonManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AddonManager extends JavaPlugin implements Listener {
	public FileConfiguration config;
	private PluginChecker checker;
	private SelfUpdater updater;
	
	public static String dlUrl = "http://www.blockmirror.com";
	public static String permPrefix = "addonmanager";
	
	public void onEnable() {
		if(!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		
		this.getConfig().options().copyDefaults(true);
        this.saveConfig();
		
		this.checker = new PluginChecker();
		this.checker.setDataFolder(getDataFolder());
		this.checker.loadConfig();
		
		if(getConfig().getBoolean("inform-on-update")) {
			this.setupSelfUpdates();
		}		

        getServer().getPluginManager().registerEvents(this, this);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			if(!((Player) sender).hasPermission(permPrefix + ".usage")) {
				sender.sendMessage("You do not have permissions to use this command.");
				return true;
			}
		}
		
		if(args.length < 1) {
			return false;
		}
		
		if(args[0].equals("details") && args.length > 1) {
			this.checker.sendDetails(args[1], sender);			
			return true;
		}
		
		if(args[0].equals("install") && args.length > 1) {
			PluginInstaller inst = new PluginInstaller();
			inst.setSender(sender);
			inst.setPluginChecker(this.checker);
			inst.addPlugin(args[1]);
			inst.start();
			
			return true;
		}
		
		if(args[0].equals("update") && args.length > 1) {
			PluginInstaller inst = new PluginInstaller();
			inst.setSender(sender);
			inst.setPluginChecker(this.checker);
			
			if(args[1].equals("all")) {
				for(String p: this.getOldPlugins()) {
					inst.addPlugin(p);
				}
			} else {
				inst.addPlugin(args[1]);
			}
			
			inst.start();
			
			return true;
		}
		
		if(args[0].equals("info")) {
			sender.sendMessage("Installed Plugins: " + Functions.joinList(this.getPlugins()));
			sender.sendMessage("Supported Plugins: " + Functions.joinList(this.getUpdateablePlugins()));
			sender.sendMessage("Not Supported Plugins: " + Functions.joinList(this.getNotUpdateablePlugins()));
			sender.sendMessage("OutDated Plugins: " + Functions.joinList(this.getOldPlugins()));
			
			return true;
		}
		
		return false;
	}
	
	public List<String> getPlugins() {
		List<String> ret = new ArrayList<String>();
		
		File pd = new File(getDataFolder().getParentFile().getPath());
		File plugins[] = pd.listFiles();
		
		for(File p: plugins) {
			if(p.getName().contains(".jar")) {
				ret.add(p.getName().substring(0, p.getName().length() - 4));
			}
		}
		
		return ret;
	}
	
	public List<String> getUpdateablePlugins() {
		List<String> ret = new ArrayList<String>();
		
		for(String p: this.getPlugins()) {
			if(this.checker.pluginAvailable(p)) {
				ret.add(p);
			}
		}
		
		return ret;
	}
	
	public List<String> getOldPlugins() {
		List<String> ret = new ArrayList<String>();
		
		for(String p: this.getUpdateablePlugins()) {
			if(!getConfig().getList("ignore-plugins").contains(p) && this.checker.updateAvailable(p)) {
				ret.add(p);
			}
		}
		
		return ret;
	}
	
	public List<String> getNotUpdateablePlugins() {
		List<String> ret = new ArrayList<String>();
		
		for(String p: this.getPlugins()) {
			if(!this.checker.pluginAvailable(p)) {
				ret.add(p);
			}
		}
		
		return ret;
	}
	
	public void loadConfig() {		
				
	}
	
	public boolean updatesAvailable() {
		return !this.getOldPlugins().isEmpty();
	}

    @EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
    	if(this.getConfig().getBoolean("inform-on-login", true)) {
    		if(event.getPlayer().hasPermission(permPrefix + ".notify") || event.getPlayer().hasPermission(permPrefix + ".notify.login")) {
    			if(this.updatesAvailable()) {
    				this.notifyPlayer(event.getPlayer());
    			}
    		}
    	}
    }
    
    public void notifyPlayer(Player p) {
		p.sendMessage(this.getName() + " has found some available updates:");
		p.sendMessage(Functions.joinList(this.getOldPlugins()));
		p.sendMessage("Update them using \"/am update all\""); 
    }
    
    public void setupSelfUpdates() {
    	this.updater = new SelfUpdater();
    	this.updater.setPlugin(this);
    	this.updater.setChecker(this.checker);
    	
    	getServer().getScheduler().scheduleAsyncRepeatingTask(this, this.updater, 30*20L, 2*60*60*20L);
    }
}
