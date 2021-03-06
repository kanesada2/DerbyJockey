package com.github.kanesada2.DerbyJockey;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class DerbyJockeyListener implements Listener {
	private DerbyJockey plugin;

	public DerbyJockeyListener(DerbyJockey plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEnter(VehicleEnterEvent e) {
		if (!(e.getVehicle() instanceof AbstractHorse && e.getEntered() instanceof Player)) {
			return;
		}
		AbstractHorse horse = (AbstractHorse) e.getVehicle();
		Player player = (Player)e.getEntered();
		if(!Util.isHorseWhip(player.getInventory().getItemInMainHand())){
			return;
		}
		BossBar bar;
		int maxSpeedLevel;
		double spanModifier;
		if(horse.hasMetadata("bar") && !horse.getMetadata("bar").isEmpty()){
			bar = (BossBar)horse.getMetadata("bar").get(0).value();
		}else{
			bar = Bukkit.getServer().createBossBar("", BarColor.GREEN, BarStyle.SOLID);
			horse.setMetadata("bar", new FixedMetadataValue(plugin, bar));
		}
		new StaminaBarManager(horse, bar, 0).init();
		bar.addPlayer(player);
		if(horse.hasMetadata("max") && horse.hasMetadata("modifier") && horse.hasMetadata("baseSpeed")){
			spanModifier = horse.getMetadata("modifier").get(0).asDouble();
			maxSpeedLevel = horse.getMetadata("max").get(0).asInt();
		}else{
			String numUUID = String.valueOf(Math.abs(horse.getUniqueId().hashCode()));
			if(numUUID.length() < 9){
				int intUUID = Integer.parseInt(numUUID);
				if(intUUID == 0){
					intUUID = 1;
				}
				for(int i=0;i<9-numUUID.length(); i++){
					intUUID *= horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				}
				numUUID = String.valueOf(intUUID);
			}
			String spec = numUUID.substring(numUUID.length() - 9);
			maxSpeedLevel = 2 + (int)(Util.getSumfromString(spec.substring(0, 4)) / 10);
			spanModifier = 1.0d - ((double)Util.getSumfromString(spec.substring(4, 9)) / 100);
			horse.setMetadata("max", new FixedMetadataValue(plugin, maxSpeedLevel));
			horse.setMetadata("modifier", new FixedMetadataValue(plugin, spanModifier));
			horse.setMetadata("baseSpeed", new FixedMetadataValue(plugin, horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue()));
		}
		int goodFeeling = (int)(Math.random() * maxSpeedLevel);
		horse.setMetadata("good", new FixedMetadataValue(plugin, goodFeeling));
		player.sendMessage("最大加速レベル: " + String.valueOf(maxSpeedLevel));
		player.sendMessage("走行継続時間: " + String.format("%1$.2f", 1 / (0.005 * spanModifier)) + "秒");
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onExit(VehicleExitEvent e){
		if(!(e.getVehicle() instanceof AbstractHorse && e.getExited() instanceof Player)){
			return;
		}
		AbstractHorse horse = (AbstractHorse)e.getVehicle();
		Player player = (Player)e.getExited();
		if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") 
		&& horse.hasMetadata("good") && horse.hasMetadata("baseSpeed"))) return;
		Util.resetJockey(horse, player);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJockeyQuit(PlayerQuitEvent e){
		if(!(e.getPlayer().isInsideVehicle() && e.getPlayer().getVehicle() instanceof AbstractHorse)) return;
		Player player = e.getPlayer();
		AbstractHorse horse = (AbstractHorse)player.getVehicle();
		if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") 
		&& horse.hasMetadata("good") && horse.hasMetadata("baseSpeed"))) return;
		Util.resetJockey(horse, player);
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onSprint(PlayerMoveEvent e){
		if(!(e.getPlayer().isInsideVehicle() && e.getPlayer().getVehicle() instanceof AbstractHorse)){
			return;
		}
		Player player = e.getPlayer();
		AbstractHorse horse = (AbstractHorse)player.getVehicle();
		if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") && horse.hasMetadata("good"))){
			return;
		}
		BossBar bar = (BossBar)horse.getMetadata("bar").get(0).value();
		double modifier = horse.getMetadata("modifier").get(0).asDouble();
		int max = horse.getMetadata("max").get(0).asInt();
		SpeedLevelManager spManager = new SpeedLevelManager(horse);
		int level = spManager.getLevel();
		if(horse.hasMetadata("targeted")){
			int count = horse.getMetadata("targeted").get(0).asInt();
			horse.removeMetadata("targeted", plugin);
			if(count > 100){
				if(level > max){
					level = spManager.decelerate();
					new StaminaBarManager(horse, bar, level).formatTitle();
				}
			}else{
				count++;
				horse.setMetadata("targeted", new FixedMetadataValue(plugin, count));
			}
		}
		if(level >= max && !horse.hasMetadata("unleashed") && Math.floor(Math.random() * 1500 / Math.pow(modifier, 5)) == 24){
			horse.removePotionEffect(PotionEffectType.SPEED);
			AttributeInstance attr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
			double unleashed = attr.getValue() * 1.1;
			double unleashedBase = horse.getMetadata("baseSpeed").get(0).asDouble() * 1.1;
			horse.setMetadata("unleashed", new FixedMetadataValue(plugin, unleashedBase));
			attr.setBaseValue(unleashed);
			horse.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, level));
			player.playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 1, 1);
			player.sendTitle(ChatColor.LIGHT_PURPLE + "POTENTIAL UNLEASHED", "", 10, 60, 10);
			new StaminaBarManager(horse, bar, level).unleash();
		}
		if(horse.hasMetadata("targeting")){
			Vehicle target = (Vehicle)horse.getMetadata("targeting").get(0).value();
			if(horse.getLocation().distance(target.getLocation()) >= 10){
				if(horse.hasMetadata("unleashed")){
					bar.setColor(BarColor.PINK);
				}else{
					bar.setColor(BarColor.GREEN);
				}
				horse.removeMetadata("targeting", plugin);
			}
		}
		if(bar.getProgress() > 0){
			new StaminaBarManager(horse, bar, level).clock();
		}else{
			horse.removePotionEffect(PotionEffectType.SPEED);
			horse.removePotionEffect(PotionEffectType.SLOW);
			if(!horse.hasMetadata("exhausted")){
				AttributeInstance attr = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
				String metadataKey = "baseSpeed";
				if(horse.hasMetadata("unleashed")){
					metadataKey = "unleashed";
				}
				double baseValue = horse.getMetadata(metadataKey).get(0).asDouble();
				double exhausted = baseValue * modifier;
				attr.setBaseValue(exhausted);
				horse.setMetadata("exhausted", new FixedMetadataValue(plugin, exhausted));
				new StaminaBarManager(horse,bar,0).exhaust();
			}
		}
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onHorseWhip(PlayerInteractEvent e){
		if(!(e.getPlayer().isInsideVehicle() && e.getPlayer().getVehicle() instanceof AbstractHorse)){
			return;
		}
		Player player = e.getPlayer();
		AbstractHorse horse = (AbstractHorse)e.getPlayer().getVehicle();
		if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") && horse.hasMetadata("good"))){
			return;
		}
		e.setCancelled(true);
		if(!Util.isHorseWhip(e.getPlayer().getInventory().getItemInMainHand())){
			return;
		}
		if(player.getVelocity().setY(0).length() == 0){
			return;
		}
		BossBar bar = (BossBar)horse.getMetadata("bar").get(0).value();
		if(bar.getProgress() == 0){
			return;
		}
		SpeedLevelManager spManager = new SpeedLevelManager(horse);
		int level = spManager.getLevel();
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1, 1);
			int max = horse.getMetadata("max").get(0).asInt();
			if(level >= max){
				return;
			}
			level = spManager.accelerate();

		}else if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(level < 0){
				return;
			}
			level = spManager.decelerate();
		}
		new StaminaBarManager(horse, bar, level).whip(e.getAction());;
	}
	@EventHandler(priority = EventPriority.LOW)
	public void onTarget(PlayerSwapHandItemsEvent e){
		Player player = e.getPlayer();
		if(!(player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse)){
			return;
		}
		AbstractHorse horse = (AbstractHorse)player.getVehicle();
		if(!(horse.hasMetadata("bar") && horse.hasMetadata("max") && horse.hasMetadata("modifier") && horse.hasMetadata("good"))){
			return;
		}
		e.setCancelled(true);
		if(player.hasMetadata("cooltime")){
			return;
		}
		if(player.getVelocity().setY(0).length() == 0){
			return;
		}
		if(horse.hasMetadata("targeting")){
			horse.removeMetadata("targeting", plugin);
		}
		Collection<Entity> entities = horse.getNearbyEntities(5, 2, 5);
		Vehicle target = null;
		for(Entity entity : entities){
			if(entity instanceof AbstractHorse && entity.hasMetadata("good")){
				target = (AbstractHorse)entity;
				break;
			}
			if(entity instanceof Minecart){
				target = (Minecart)entity;
			}
		}
		if(target != null){
			horse.setMetadata("targeting", new FixedMetadataValue(plugin, target));
			BossBar bar = (BossBar)horse.getMetadata("bar").get(0).value();
			bar.setColor(BarColor.RED);
			Vector toTarget = target.getLocation().subtract(horse.getLocation()).toVector().setY(0);
			if(toTarget.dot(horse.getLocation().getDirection().setY(0)) > 0 && !target.hasMetadata("targeted")){
				if(target instanceof AbstractHorse){
					target.setMetadata("targeted", new FixedMetadataValue(plugin, 0));
					AbstractHorse horseTarget = (AbstractHorse)target;
					int targetLv = new SpeedLevelManager(horseTarget).accelerate();
					BossBar targetBar = (BossBar)target.getMetadata("bar").get(0).value();
					new StaminaBarManager(horseTarget, targetBar, targetLv).formatTitle();
				}
			}
		}
		player.setMetadata("cooltime", new FixedMetadataValue(plugin, true));
		new PlayerCoolDownTask(plugin,player).runTaskLater(plugin, 20);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onWeight(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player)) return;
		if(!(e.getInventory().getHolder() instanceof Horse)) return;
		final Horse horse = (Horse)e.getInventory().getHolder();
		final Player player = (Player)e.getWhoClicked();
		plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable(){
			@Override
			public void run() {
				int prevWeight = 0;
				if(horse.hasMetadata("impost")){
					prevWeight = horse.getMetadata("impost").get(0).asInt();
					horse.removeMetadata("impost", plugin);
				}
				String msg = player.getName() + " set " + horse.getName() + "'s impost to ";
				if(horse.getInventory().getArmor() == null || !Util.isWeightedArmor(horse.getInventory().getArmor())){
					if(prevWeight == 0) return;
					msg = msg + "0";
				}else{
					int weight = Util.getWeightCount(horse.getInventory().getArmor());
					horse.setMetadata("impost", new FixedMetadataValue(plugin, weight));
					if(weight == prevWeight) return;
					msg  = msg + weight;
				}
				Util.broadcastRange(player, msg, 20);
			}
		}, 1L);
	}

	//For Debugging "targeting" fearture alone
	/*@EventHandler(priority = EventPriority.LOW)
	public void onTarget(PlayerDropItemEvent e){
		Player player = (Player)e.getPlayer();
		if(!(player.isInsideVehicle() && player.getVehicle() instanceof AbstractHorse)){
			return;
		}
		AbstractHorse target = (AbstractHorse)player.getVehicle();
		if(!(target.hasMetadata("bar") && target.hasMetadata("max") && target.hasMetadata("modifier"))){
			return;
		}
		if(!target.hasMetadata("targeted")){
			target.setMetadata("targeted", new FixedMetadataValue(plugin, 0));
			int targetLv = new SpeedLevelManager(target).accelerate();
			BossBar targetBar = (BossBar)target.getMetadata("bar").get(0).value();
			new StaminaBarManager(target, targetBar, targetLv).formatTitle();
			target.setMetadata("targeting", new FixedMetadataValue(plugin, target));
			targetBar.setColor(BarColor.RED);
		}
	}*/
}
