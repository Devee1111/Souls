package me.Devee1111;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;


/*
 * 
 * Made 5/7/2021 WHILE DRUNK CHECK IT LATER
 * 
 */
public class mobHeads implements Listener {
	
	Souls inst;
	
	public mobHeads(Souls inst) {
		this.inst = inst;
	}

	
	@EventHandler
	public void onMobKill(EntityDeathEvent e) {
		
		//Makes sure a player triggered the event
		if(e.getEntity().getKiller() == null) { return; }
		//Only process mobs in config
		if(!inst.getConfig().getList("record").contains(e.getEntityType().toString())) { return; }
		
		
		
		
	}
	
	
}
