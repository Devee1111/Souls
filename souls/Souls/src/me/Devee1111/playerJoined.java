package me.Devee1111;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class playerJoined implements Listener {
	
	Souls inst;
	
	public playerJoined(Souls inst) {
		this.inst = inst;
		
	}
	

	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		File pfile = new File(inst.getDataFolder().getAbsolutePath()+"/users/"+e.getPlayer().getUniqueId()+".yml");
		
		//MAKING SURE DIRECTORY EXISTS
		if(pfile.getParentFile().mkdir()) {
			inst.getLogger().log(Level.INFO,"Directory 'plugins/souls/users/' not found...");
			inst.getLogger().log(Level.INFO,"Creating directory plugins/souls/users");
		}
		
		//Checking if file exists
		if(!pfile.exists()) {
			try {
				pfile.createNewFile();
			} catch (IOException e1) { 
				/*FAILED TO CREATE FILE*/
				e1.printStackTrace(); 
				return;
			}
			
		}
		
		//Converting file into a configuration
		new YamlConfiguration();
		YamlConfiguration pconfig = YamlConfiguration.loadConfiguration(pfile);
		
		//Setting the current name in config
		pconfig.set("name", e.getPlayer().getName());
		
		//Saving config
		try {
			pconfig.save(pfile);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}
