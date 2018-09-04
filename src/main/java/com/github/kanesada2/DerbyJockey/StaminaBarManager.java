package com.github.kanesada2.DerbyJockey;

import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Horse;
import org.bukkit.event.block.Action;

public class StaminaBarManager {
	private Horse horse;
	private BossBar bar;
	private int level;
	public StaminaBarManager(Horse horse, BossBar bar, int level){
		this.horse = horse;
		this.bar = bar;
		this.level = level;
	}
	public void init(){
		bar.setProgress(1);
		bar.setColor(BarColor.GREEN);
		formatTitle();
	}
	public void clock(){
		double modifier = horse.getMetadata("modifier").get(0).asDouble();
		int multipiler = level;
		if(multipiler < 1){
			modifier = 0.6;
		}else{
			if(horse.hasMetadata("targeting")){
				modifier = modifier * (1 - Math.pow(0.3, multipiler));
			}
			if(level == horse.getMetadata("good").get(0).asInt()){
				modifier = modifier * (1 - Math.pow(0.3, multipiler));
			}
		}
		double span = 0.00025 * modifier;
		if(level >= 0){
			span += span * level;
		}else{
			span *= 0.5;
		}
		if(bar.getProgress() <= span){
			bar.setProgress(0);
		}else{
			bar.setProgress(bar.getProgress() - span);
		}
	}
	public void whip(Action action){
		double span = 0.05;
		if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
			if(level != 0){
				span = span / level;
			}
		}else if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
			span = span / 2;
		}
		formatTitle();
		if(horse.hasMetadata("targeting") || horse.hasMetadata("unleashed")){
			return;
		}
		if(bar.getProgress() < span){
			bar.setProgress(0);
		}else{
			bar.setProgress(bar.getProgress() - span);
		}
	}
	public void unleash(){
		bar.setColor(BarColor.PINK);
		double stamina = (bar.getProgress() + 0.1) * 1.5;
		if(stamina > 1){
			stamina = 1;
		}
		bar.setProgress(stamina);
		formatTitle();
	}
	public void exhaust(){
		level = 0;
		bar.setProgress(0);
		bar.setColor(BarColor.GREEN);
		formatTitle();
	}
	public void formatTitle(){
		String title = "Horse";
		if(horse.getCustomName() != null){
			title = horse.getCustomName();
		}
		if(level != 0){
			title = title + ChatColor.AQUA + ChatColor.BOLD +"  SPLv:" + String.valueOf(level);
		}
		if(horse.hasMetadata("exhausted")){
			title = title + ChatColor.DARK_RED + ChatColor.BOLD + "  EXHAUSTED";
		}
		if(horse.hasMetadata("unleashed")){
			title = title + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "  UNLEASHED";
		}
		bar.setTitle(title);
	}
}
