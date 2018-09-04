package com.github.kanesada2.DerbyJockey;

import org.bukkit.entity.Horse;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedLevelManager {
	private Horse horse;
	private int level = 0;
	public SpeedLevelManager(Horse horse){
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
		return level;
	}
}
