package me.Devee1111;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Souls extends JavaPlugin {
	
	public Souls inst;
	
	@Override
	public void onEnable() {
		
		loadConfig();
		inst = this;
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new mobKills(this), this);
		pm.registerEvents(new playerJoined(this), this);
		pm.registerEvents(new mobEggCapture(this), this);
		
		getCommand("soul").setExecutor(new cmdSouls(inst));
		
		//This should be deleted
		pm.registerEvents(new mobHeads(this), this);
		
		
	}
	
	
	@Override
	public void onDisable() { //UWU	
	}
	
	/*
	 * This is the global item used for checking and giving eggs to ensure it's consistent and accurate
	 * Egg still needs lore added, and make the item name changeable in the config with current and past names
	 */
	public ItemStack getEgg() {
		ItemStack egg = new ItemStack(Material.EGG);
		egg.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		//TODO make the lore work
		egg.getItemMeta().setDisplayName("§4Soul §begg");
		List<String> lore = new ArrayList<>();
		lore.add("This is some test lore~");
		//lore.set(0, "This will capture the very soul of an entity given you've slain enough.);
		egg.getItemMeta().setLore(lore);
		return egg;
	}
	
	//This is the global item we'll be using to replace iron golem spawn eggs because they don't exist. Lore still needs work.
	public ItemStack getGolemEgg() {
		ItemStack egg = new ItemStack(Material.EGG);
		egg.getItemMeta().setDisplayName("§7Iron Golem §cSoul");
		//insert lore where it tells them that it can be used to craft an iron golem spawner 
		return egg;
	}
	
	public void loadConfig() {
		saveDefaultConfig();
		double configVersion = getConfig().getDouble("version");
		//Current version is 0.5, set to anything else while compiling to stay in debug
		if(!(configVersion == 0.5)) {
			saveResource("config.yml", true);
			this.reloadConfig();
		} 
	}
	
	public void reloadConfiguration() {
		this.reloadConfig();
	}
	
	public void debug(String message) {
		if(getConfig().getBoolean("debug") == true) {
			getServer().getLogger().log(Level.INFO, "[Souls]-[DEBUG] | "+message);
		}
	}
	
	public void message(String path, Player p) {
		String mess = getConfig().getString("messages."+path);
		mess = getConfig().getString("messages.prefix")+mess;
		debug(mess);
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', mess));
	}
	
	public Souls getInstance() {
		return inst;
	}

}
