package com.github.kanesada2.DerbyJockey;

import org.bukkit.plugin.java.JavaPlugin;

public class DerbyJockey extends JavaPlugin {

    private DerbyJockeyListener listener;

	@Override
    public void onEnable() {
        listener = new DerbyJockeyListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        getServer().addRecipe(Util.getHorseWhipRecipe());
        for(int count=1; count <= 8; count++){
            getServer().addRecipe(Util.getWeightedArmorRecipe(count));
        }
        getLogger().info("DerbyJockey Enabled!");
    }

    @Override
    public void onDisable() {

    }
}
