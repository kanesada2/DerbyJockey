package com.github.kanesada2.DerbyJockey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public final class Util {
	private Util(){}
	private static DerbyJockey plugin = DerbyJockey.getPlugin(DerbyJockey.class);
	public static boolean isMyItem(ItemStack item){
		if(!item.hasItemMeta()){
			return false;
		}
		ItemMeta itemMeta = item.getItemMeta();
		return itemMeta.hasLore() && itemMeta.getLore().contains("DerbyJockey Item");
	}

	public static boolean isHorseWhip(ItemStack item){
		return item.getType() == Material.STICK && isMyItem(item);
	}

	public static boolean isWeightedArmor(ItemStack item){
		return item.getType() == Material.IRON_HORSE_ARMOR && isMyItem(item);
	}

	public static ItemStack getHorseWhip(){
		ItemStack horseWhip = new ItemStack(Material.STICK);
		ItemMeta Meta = horseWhip.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("DerbyJockey Item");
		lore.add("HorseWhip");
		Meta.setLore(lore);
		Meta.setDisplayName("Horsewhip");
		horseWhip.setItemMeta(Meta);
		return horseWhip;
	}

	public static ItemStack getWeightedArmor(int weightCount){
		ItemStack weightedArmor = new ItemStack(Material.IRON_HORSE_ARMOR);
		ItemMeta meta = weightedArmor.getItemMeta();
		List<String> lore = new ArrayList<String>();
		lore.add("DerbyJockey Item");
		lore.add("impost:+" + weightCount);
		meta.setLore(lore);
		meta.setDisplayName("Stamina -" + (10 * weightCount) + "%");
		weightedArmor.setItemMeta(meta);
		return weightedArmor;
	}

	public static ShapelessRecipe getHorseWhipRecipe(){
		ItemStack horseWhip = getHorseWhip();
		NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + horseWhip.getItemMeta().getDisplayName());

		ShapelessRecipe Recipe = new ShapelessRecipe(key, horseWhip); 
		Recipe.addIngredient(Material.STICK);
		Recipe.addIngredient(Material.LEATHER);
		return Recipe;
	}

	public static ShapelessRecipe getWeightedArmorRecipe(int weightCount){
		ItemStack weightedArmor = getWeightedArmor(weightCount);
		NamespacedKey key = new NamespacedKey(plugin, plugin.getDescription().getName() + "weightedAromor" + weightCount);
		ShapelessRecipe Recipe = new ShapelessRecipe(key, weightedArmor); 
		Recipe.addIngredient(Material.IRON_HORSE_ARMOR);
		for(int i=1; i <= weightCount; i++){
			Recipe.addIngredient(Material.IRON_INGOT);
		}
		return Recipe;
	}

	public static int getWeightCount(ItemStack item){
		if(!isWeightedArmor(item)) return 0;
		List<String> lore = item.getItemMeta().getLore();
		String cntStr = lore.get(1).split(":")[1];
		int count = Integer.parseInt(cntStr);
		return count;
	}

	public static int getSumfromString(String str){
		int sum = 0;
		for(int i=0;i<str.length();i++){
			sum += Character.getNumericValue(str.charAt(i));
		}
		return sum;
	}

	public static void broadcastRange(Entity sender, String msg, int range){
		range *= range;
		Location location = sender.getLocation();
		List<Player> players = sender.getWorld().getPlayers();
		for(Player player : players){
			  if(range > 0 && location.distanceSquared(player.getLocation()) > range){
				  continue;
			  }
			player.sendMessage(msg);
		}
	}

	public static void resetJockey(AbstractHorse horse, Player player)
	{
		BossBar bar = (BossBar)horse.getMetadata("bar").get(0).value();
		bar.removeAll();
		horse.removePotionEffect(PotionEffectType.SPEED);
		horse.removePotionEffect(PotionEffectType.SLOW);
		horse.removeMetadata("good", plugin);
		AttributeInstance attr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		double nowSpeed = attr.getValue();
		double baseSpeed = horse.getMetadata("baseSpeed").get(0).asDouble();
		if(nowSpeed != baseSpeed){
			attr.setBaseValue(baseSpeed);
		}
		if(horse.hasMetadata("exhausted")){
			horse.removeMetadata("exhausted", plugin);
		}
		if(horse.hasMetadata("targeting")){
			horse.removeMetadata("targeting", plugin);
		}
		if(horse.hasMetadata("targeted")){
			horse.removeMetadata("targeted", plugin);
		}
		if(horse.hasMetadata("raised")){
			horse.removeMetadata("raised", plugin);
		}
		if(player.hasMetadata("cooltime")){
			player.removeMetadata("cooltime", plugin);
		}
		if(horse.hasMetadata("unleashed")){
			horse.removeMetadata("unleashed", plugin);
		}
	}

	public static void resetWorldJockeys(World world)
	{
		Collection<Player> players = world.getEntitiesByClass(Player.class);
		for(Player player : players){
			if(!(player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse)) continue;
			AbstractHorse horse = (AbstractHorse)player.getVehicle();
			if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") 
				&& horse.hasMetadata("good") && horse.hasMetadata("baseSpeed"))) continue;
			resetJockey(horse, player);
		}
	}
}
