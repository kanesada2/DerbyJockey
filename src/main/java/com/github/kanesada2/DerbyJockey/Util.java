package com.github.kanesada2.DerbyJockey;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public final class Util {
	private Util(){}
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
		double weightPercent = 100 - 10 * weightCount;
		meta.setDisplayName("Stamina - " + weightPercent + "%");
		return weightedArmor;
	}

	public static ShapelessRecipe getHorseWhipRecipe(NamespacedKey key){
		ItemStack horseWhip = getHorseWhip();

		ShapelessRecipe Recipe = new ShapelessRecipe(key, horseWhip); 
		Recipe.addIngredient(Material.STICK);
		Recipe.addIngredient(Material.LEATHER);
		return Recipe;
	}

	public static ShapelessRecipe getWeightedArmorRecipe(NamespacedKey key, int weightCount){
		ItemStack weightedArmor = getWeightedArmor(weightCount);

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
}
