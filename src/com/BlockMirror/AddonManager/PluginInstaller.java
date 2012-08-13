package com.BlockMirror.AddonManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

public class PluginInstaller extends Thread {
	private List<String> plugins = new ArrayList<String>();
	private PluginChecker checker;
	private CommandSender sender;
	
	public PluginInstaller() {
		
	}
	
	public void setPluginChecker(PluginChecker pc) {
		this.checker = pc;
	}
	
	public void setSender(CommandSender s) {
		this.sender = s;
	}
	
	public void addPlugin(String name) {
		if(!this.checker.pluginAvailable(name)) {
			sender.sendMessage("Plugin " + name + " is not available for auto-updating");
			return;
		}
		
		this.plugins.add(name);
	}
	
	public void run() {
		for(String name: this.plugins) {
			// System.out.println(item);
			String fn = this.checker.config.getString(name.toLowerCase() + ".filename") + ".jar";
			Functions.downloadFile(AddonManager.dlUrl + "/plugins/" + fn, this.checker.DataFolder.getParentFile().getPath()+ File.separator + fn);
			sender.sendMessage("Plugin " + name + " installed successfully");
		}
	}
}
