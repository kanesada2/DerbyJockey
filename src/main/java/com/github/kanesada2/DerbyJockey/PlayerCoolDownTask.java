package com.github.kanesada2.DerbyJockey;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCoolDownTask extends BukkitRunnable {
	private DerbyJockey plugin;
	private Player player;
	public PlayerCoolDownTask(DerbyJockey plugin, Player player){
		this.plugin = plugin;
		this.player = player;
	}
	@Override
	public void run() {
		if(player.hasMetadata("cooltime")){
			player.removeMetadata("cooltime", plugin);
		}
	}

}