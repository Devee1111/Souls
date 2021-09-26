package me.Devee1111;


import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;



public class mobEggCapture implements Listener {
	
	
	Souls inst;
	
	public mobEggCapture(Souls inst) {
		this.inst = inst;
	 }
	
	
	@EventHandler
	public void onEggHatch(PlayerEggThrowEvent e) {
		ItemStack eggItem = e.getEgg().getItem();
		if(eggItem.equals(inst.getEgg())) {
			e.setHatching(false);
		} 
	}
	
	
	@EventHandler (priority = EventPriority.HIGH)
	public void onEggHit(ProjectileHitEvent e) {
		//Making sure we only process eggs.
		if(!(e.getEntityType() == EntityType.EGG)) { return; }
		Egg egg = (Egg) e.getEntity();
		ItemStack eggItem = (ItemStack) egg.getItem();
		//Checks to ensure its our plugin egg thrown, has hit an entity, is being charged, isnt from spawner, and is thrown by player
		if(!eggItem.equals(inst.getEgg())) { return; }
		if(e.getHitEntity() == null) {
			inst.debug("No entity hit.");
			Location blockLoc = e.getHitBlock().getLocation();
			returnEgg(blockLoc);
			return; }
		Location loc = e.getHitEntity().getLocation();
		if(!inst.getConfig().getList("record").contains(e.getHitEntity().getType().toString())) {
			inst.debug("Entity is not being recorded: "+e.getHitEntity().getType().toString());
			returnEgg(loc);
			return; }
		if(e.getHitEntity().hasMetadata("SPAWNREASON")) {
			inst.debug("Entity can't be captured! Spawn_Reason = spawner");
			//TODO Inform player eggs only work on natural mobs 
			returnEgg(loc);
			return; }
		//TODO fix this check to make sure a player threw the egg so we dont cast dispensers and shit
//		if(e.getEntity().getShooter() instanceof Player) { 
//			returnEgg(loc);
//			return; }
		/*
		 * TEMP FIX FOR IRON GOLEMS
		 */
//		if(e.getEntityType().toString().equalsIgnoreCase("IRON_GOLEM")) {
//			Player p = (Player) e.getEntity();
//			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&4Souls&8] &cError! &bThis mob currently can't be captured, however it's souls are still recorded."));
//			inst.debug("Iron_Golem cannot be captured");
//			returnEgg(loc);
//			return;
//		}
		
		
		//Spitting out debug information
		inst.debug("Entity = "+e.getEntityType().toString());
		
		//Checking if they have the souls required section
		Player p = (Player) e.getEntity().getShooter();
		inst.debug("Player name = "+p.getName());
		int price = inst.getConfig().getInt("cost.default.price");
		//getting values required for capture
		if(inst.getConfig().contains("cost."+e.getEntityType().toString()+".price")) {
			price = inst.getConfig().getInt("cost."+e.getEntityType().toString()+".price");
		}
		
		//If there is no price to capture, don't check files and just stop here
		if(price == 0) {
			LivingEntity mob = (LivingEntity) e.getHitEntity();
			mob.setMetadata("NoDrops", new FixedMetadataValue(inst, "data"));
			mob.setHealth(0);
			return; }
		
		//Getting player data
		File pfile = new File(inst.getDataFolder().getAbsolutePath()+"/users/"+p.getUniqueId()+".yml");
		new YamlConfiguration();
		YamlConfiguration pdata = YamlConfiguration.loadConfiguration(pfile);
		//Checking soul count
		if(pdata.contains("kills."+e.getHitEntity().getType().toString())) {} else {
			inst.debug("Player has no record kills of "+e.getHitEntity().getType().toString());
			returnEgg(loc);
			inst.message("notEnoughSouls", p); //TODO Include variables later
			return;
		}
		
		//Getting currently captured souls, and checking if they have enough
		int souls = pdata.getInt("kills."+e.getHitEntity().getType().toString());
		inst.debug("Souls = "+souls);
		inst.debug("Price = "+price);
		if(souls < price) {
			returnEgg(loc);
			inst.message("notEnoughSouls", p); //TODO Include variables later
			return;
		}
		
		//At this point they have enough souls, take them away && continue the process
		souls = souls - price;
		pdata.set("kills."+e.getHitEntity().getType().toString(), souls);
		
		//TODO Inform soul was captured
		
		//Saving player config file
		try {
			pdata.save(pfile);
		} catch (IOException error) {
			error.printStackTrace();
		}
	
		//At this point, the mob is okay to make into a spawn egg and is ready
		LivingEntity mob = (LivingEntity) e.getHitEntity();
		mob.setMetadata("NoDrops", new FixedMetadataValue(inst, "data"));
		mob.setHealth(0);
	}
	
	
	@EventHandler
	public void onEggDeath(EntityDeathEvent e) {
		if(e.getEntity().hasMetadata("NoDrops")) {
			e.getDrops().clear();
			e.setDroppedExp(0);
			//This section is how we handle special cases I.E Iron Golems or mobs that don't have spawn eggs
			if(e.getEntityType().toString().equalsIgnoreCase("IRON_GOLEM")) {
				inst.debug("Special mob detected: Iron_Golem. No mob egg exists, generating soul object instead.");
				ItemStack soul = inst.getGolemEgg();
				e.getDrops().add(soul);
				return;
			}
			//Mobs that have spawn eggs will be handled here like normal
			Material eggMat = Material.getMaterial(e.getEntityType().toString()+"_SPAWN_EGG");
			inst.debug("Creating spawn egg: "+e.getEntityType().toString()+"_SPAWN_EGG");
			ItemStack egg = new ItemStack(eggMat);
			e.getDrops().add((egg));
		}
	}
	
	//TODO put it back into player inventory instead and droo in world when full 
	public void returnEgg(Location loc) {
		loc.getWorld().dropItem(loc, inst.getEgg());
	}
}
