package com.github.kanesada2.DerbyJockey;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class DerbyJockey extends JavaPlugin {

    private DerbyJockeyListener listener;
    private NamespacedKey nameKey;

	@Override
    public void onEnable() {
        listener = new DerbyJockeyListener(this);
        getServer().getPluginManager().registerEvents(listener, this);
        nameKey = new NamespacedKey(this, "DerbyJockey");
        getServer().addRecipe(Util.getHorseWhipRecipe(nameKey));
        for(int count=1; count <= 8; count++){
            getServer().addRecipe(Util.getWeightedArmorRecipe(nameKey, count));
        }
        getLogger().info("DerbyJockey Enabled!");
    }

    @Override
    public void onDisable() {

    }
}
