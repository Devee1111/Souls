package me.Devee1111;


import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class mobKills implements Listener {
	
	
	Souls inst;
	
	public mobKills(Souls inst) {
		this.inst = inst;
	}
	
	@EventHandler
	//Can also be called with SpawnerSpawnEvent
	public void onSpawn(CreatureSpawnEvent e) {
		//We only store metadata if it was made by a spawner, no reason to store otherwise ~yet~
		if(e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
			e.getEntity().setMetadata("SPAWNREASON", new FixedMetadataValue(inst, "spawner"));
		}
	}
	
	@EventHandler
	public void onKill(EntityDeathEvent e) {
		
		//Makes sure a player triggered the event
		if(e.getEntity().getKiller() == null) { return; }
		//Only process mobs in config
		if(!inst.getConfig().getList("record").contains(e.getEntityType().toString())) { return; }
		//Making sure it wasn't created by a spawner
		if(e.getEntity().hasMetadata("SPAWNREASON")) {
			return;
		}
		
		//getting player object
		Player p = e.getEntity().getKiller();
		
		/*
		 * Everything past this point is okay and should be processed a kill that needs to be recorded
		 */
		
		//Getting the Player file, then converting to config
		File pfile = new File(inst.getDataFolder().getAbsolutePath()+"/users/"+p.getUniqueId()+".yml");
		new YamlConfiguration();
		YamlConfiguration pconfig = YamlConfiguration.loadConfiguration(pfile);
		
		//Getting & updating the kill count
		String path = "kills."+e.getEntityType().toString();
		int kc = pconfig.getInt(path);
		kc++;
		pconfig.set(path, kc);
		
		//Saving player config file
		try {
			pconfig.save(pfile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
