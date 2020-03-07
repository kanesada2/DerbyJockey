package com.github.kanesada2.DerbyJockey;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedLevelManager {
	private AbstractHorse horse;
	private int level = 0;
	public SpeedLevelManager(AbstractHorse horse){
		this.horse = horse;
		if(horse.hasPotionEffect(PotionEffectType.SPEED)){
			this.level = horse.getPotionEffect(PotionEffectType.SPEED).getAmplifier();
		}else if(horse.hasPotionEffect(PotionEffectType.SLOW)){
			this.level = -1;
		}
	}
	public int getLevel() {
		return level;
	}

	public int accelerate(){
		if(horse.hasMetadata("exhausted")){
			return 0;
		}
		level = level + 1;
		if(level > 0){
			horse.removePotionEffect(PotionEffectType.SPEED);
			horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level));
		}else{
			horse.removePotionEffect(PotionEffectType.SLOW);
		}
		double maxLevel = horse.getMetadata("max").get(0).asDouble();
		if(level >= maxLevel && !horse.hasMetadata("raised")){
			double raisedModifier = (horse.getMetadata("modifier").get(0).asDouble() - 0.55) * 0.2 + 1;
			AttributeInstance attr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
			double raised = attr.getBaseValue() * raisedModifier;
			attr.setBaseValue(raised);
			horse.setMetadata("raised", new FixedMetadataValue(DerbyJockey.getPlugin(DerbyJockey.class), raised));
		}
		return level;
	}

	public int decelerate(){
		if(horse.hasMetadata("exhausted")){
			return 0;
		}
		level = level - 1;
		if(level == -1){
			horse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
		}else{
			horse.removePotionEffect(PotionEffectType.SPEED);
			if(level > 0){
				horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level));
			}
		}
		double maxLevel = horse.getMetadata("max").get(0).asInt();
		if(level < maxLevel && horse.hasMetadata("raised")){
			horse.removeMetadata("raised", DerbyJockey.getPlugin(DerbyJockey.class));
			String metadataKey = "baseSpeed";
			if(horse.hasMetadata("unleashed")){
				metadataKey = "unleashed";
			}
			horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(horse.getMetadata(metadataKey).get(0).asDouble());
		}
		return level;
	}
}
