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
	public static boolean isHorseWhip(ItemStack item){
			 if(!item.hasItemMeta()){
				 return false;
			 }
			 ItemMeta itemMeta = item.getItemMeta();
			 return itemMeta.hasLore() && itemMeta.getLore().contains("DerbyJockey Item");
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
	public static ShapelessRecipe getHorseWhipRecipe(NamespacedKey key){
		 ItemStack horseWhip = getHorseWhip();

		 ShapelessRecipe Recipe = new ShapelessRecipe(key, horseWhip); 
		 Recipe.addIngredient(Material.STICK);
		 Recipe.addIngredient(Material.LEATHER);
		 return Recipe;
	}
	public static int getSumfromString(String str){
		int sum = 0;
		for(int i=0;i<str.length();i++){
			sum += Character.getNumericValue(str.charAt(i));
		}
		return sum;
	}
}
