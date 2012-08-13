package com.BlockMirror.AddonManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SelfUpdater implements Runnable {
	private AddonManager plugin;
	private PluginChecker checker;	
	
	public void setPlugin(AddonManager p) {
		this.plugin = p;
	}
	
	public void setChecker(PluginChecker ch) {
		this.checker = ch;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.checker.updateConfig();
		
		if(this.plugin.updatesAvailable()) {
			for(Player p: Bukkit.getServer().getOnlinePlayers()) {
				if(p.hasPermission(AddonManager.permPrefix + ".notify.update")) {
					this.plugin.notifyPlayer(p);
				}
			}
		}
	}

}
