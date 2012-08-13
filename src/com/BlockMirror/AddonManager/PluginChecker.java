package com.BlockMirror.AddonManager;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PluginChecker {
	public FileConfiguration config;
	public File DataFolder;
	
	public void loadConfig() {
		File configFile;
		configFile = new File(this.DataFolder + File.separator + "plugins.yml");
		if(!configFile.exists()) {
			this.updateConfig();
			this.loadConfig();
		}
		
		this.config = YamlConfiguration.loadConfiguration(configFile);
		try {
			this.config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateConfig() {
		Functions.downloadFile(AddonManager.dlUrl + "/plugins/plugins.yml", this.DataFolder + File.separator + "plugins.yml");
		this.loadConfig();
	}
	
	public void setDataFolder(File folder) {
		this.DataFolder = folder;
	}
	
	public boolean pluginAvailable(String name) {
		name = name.toLowerCase();
		return this.config.isSet(name.toLowerCase());
	}
	
	public boolean updateAvailable(String name) {
		name = name.toLowerCase();
		File file = new File(this.DataFolder.getParentFile().getPath() + File.separator + this.config.getString(name + ".filename") + ".jar");
		return file.length() != this.config.getInt(name + ".filesize");
	}
	
	public void sendDetails(String name, CommandSender sender) {
		name = name.toLowerCase();
		if(!this.pluginAvailable(name)) {
			sender.sendMessage("Plugin " + name + " is not available for auto-updating");
			return;
		}
		
		sender.sendMessage("Plugin " + this.config.getString(name + ".filename"));
		
		//Filesize
		File file = new File(this.DataFolder.getParentFile().getPath() + File.separator + name + ".jar");
		if(file.exists()) {
			sender.sendMessage("Local filesize: " + file.length());
		} else {
			sender.sendMessage("Local filesize: Not installed on your server");
		}
		sender.sendMessage("Filesize on our servers: " + this.config.getString(name + ".filesize"));
		
		//Status
		if(this.updateAvailable(name)) {
			sender.sendMessage("Status: Update available");
		} else {
			sender.sendMessage("Status: Up to date");
		}
		
		//Version
		sender.sendMessage("Version: " + this.config.getString(name + ".version"));
	}
	
	public int getConfigAge() {
		File configFile;
		configFile = new File(this.DataFolder + File.separator + "plugins.yml");

		long timeDiffMs;
		timeDiffMs = new Date().getTime() - configFile.lastModified();
		
		return (int) Math.abs(timeDiffMs / 1000);		
	}
}
