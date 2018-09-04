package com.github.kanesada2.DerbyJockey;

import org.bukkit.plugin.java.JavaPlugin;

public class DerbyJockey extends JavaPlugin {

	private DerbyJockeyListener listener;

	@Override
    public void onEnable() {
        listener = new DerbyJockeyListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        getLogger().info("DerbyJockey Enabled!");
        getServer().addRecipe(Util.getHorseWhipRecipe());
    }

    @Override
    public void onDisable() {

    }
}
