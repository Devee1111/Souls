package me.Devee1111;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class cmdSouls implements CommandExecutor {
	
	
	
	Souls inst;
	
	public cmdSouls(Souls inst) {
		this.inst = inst;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if((cmd.getName().equalsIgnoreCase("soul" )) == false) {
			return false;
		}
		if(args.length == 0) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&',inst.getConfig().getString("messages.playerNotOnline")));
			return true;
		}
		
		inst.getConfig().get("version");

		
		if(args.length == 1) {
			//Technical stuff
			if(args[0].equalsIgnoreCase("reload")) {
				if(sender.hasPermission("souls.cmd.reload")) {
					inst.reloadConfiguration();
					sender.sendMessage(color("&aSucess! Config has been reloaded."));
				} else {
					Player p = (Player) sender;
					inst.message("noPermission", p);
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("debug")) {
				if(sender.hasPermission("souls.cmd.debug")) {
					if(inst.getConfig().getBoolean("debug") == true) {
						inst.getConfig().set("debug", false);
						sender.sendMessage(color("&4Souls &bdebug has been &cDisabled."));
					} else {
						inst.getConfig().set("debug", true);
						sender.sendMessage(color("&4Souls &bdebug has been &aEnabled."));
					}
				} else {
					Player p = (Player) sender;
					inst.message("noPermission", p);
				}
				return true;
			}
			
			if(args[0].equalsIgnoreCase("giveegg")) {
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', inst.getConfig().getString("messages.playerOnly")));
					return true; }
				Player p = (Player) sender;
				inst.debug("Name = "+p.getName());
				if(sender.hasPermission("souls.cmd.giveegg")) {
					p.getInventory().addItem(inst.getEgg());
					inst.message("eggReceived", p);
					return true; }
				inst.message("noPermission", p);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("list")) {
				Player p = (Player) sender;
				showList(p, 1);
				return true;
			}
			
			if(args[0].equalsIgnoreCase("cost")) {
				Player p = (Player) sender;
				showCost(p,1);
				return true;
			}
		}
		
		
		if(args.length == 2) {
			if(args[0].equalsIgnoreCase("list")) {
				Player p = (Player) sender;//TODO only players should be able to run this command but to save time we're just assuming a player is
				int page = 1;
				try {
					page = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					inst.message("intRequired",p);
					return true; }
				showList(p, page);
				return true;
			}
			if(args[0].equalsIgnoreCase("cost")) {
				Player p = (Player) sender;// a player and console can run this command but to save time we just assume player
				int page = 0;
				try {
					page = Integer.parseInt(args[1]);
				} catch(NumberFormatException e) {
					inst.message("intRequired",p);
					return true; }
				showCost(p, page);
				return true;
			}
		}
		
		
		if(args.length == 3) {
			if(args[0].equalsIgnoreCase("giveegg")) {
				Player p = (Player) sender;
				if(sender.hasPermission("souls.cmd.giveegg") == false) {
					inst.message("noPermission", p);
					return true;
				}
				if(inst.getServer().getPlayerExact(args[1]) == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&',inst.getConfig().getString("messages.playerNotOnline")));
					return true; }
				Player rec = inst.getServer().getPlayerExact(args[1]);
				int amount = 1;
				try {
					amount = Integer.parseInt(args[2]);
				} catch(NumberFormatException e) {
					inst.message("intRequired",p);
					return true; }
				ItemStack egg = inst.getEgg();
				egg.setAmount(amount);
				rec.getInventory().addItem(egg);
				inst.message("eggReceived", rec);
				return true;
			}	
		}
		
		
		if(args.length == 4) {
			if(args[0].equalsIgnoreCase("give")) {
				if(sender.hasPermission("souls.cmd.give") == false) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', inst.getConfig().getString("messages.noPermission")));
					return true;
				}
				if(inst.getServer().getPlayerExact(args[1]) == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', inst.getConfig().getString("messages.playerNotOnline")));
					return true; }
				Player rec = inst.getServer().getPlayerExact(args[1]);
				if(EntityType.valueOf(args[2].toUpperCase()) == null) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', inst.getConfig().getString("messages.unrealEntity")));
					return true;
				}
				int amount = 1;
				try {
					amount = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					Player p = (Player) sender;
					inst.message("intRequired",p);
					return true;
				}
				File pfile = new File(inst.getDataFolder().getAbsolutePath()+"/users/"+rec.getUniqueId()+".yml");
				new YamlConfiguration();
				YamlConfiguration pconfig = YamlConfiguration.loadConfiguration(pfile);
				int kills = pconfig.getInt("kills."+args[2].toUpperCase());
				kills = kills+amount;
				pconfig.set("kills."+args[2].toUpperCase(), kills);
				try {
					pconfig.save(pfile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//Send message that souls were given
				return true;
				
			}
		}
		
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',inst.getConfig().getString("messages.help")));
		return true;
	}

	
	//TODO make messages configurable on showList and showCost
	private void showList(Player p, int page) {
		int lim = 8;
		
		File pfile = new File(inst.getDataFolder().getAbsolutePath()+"/users/"+p.getUniqueId()+".yml");
		new YamlConfiguration();
		YamlConfiguration pconfig = YamlConfiguration.loadConfiguration(pfile);
		
		List<String> souls = new ArrayList<String>();
		for(String s : pconfig.getConfigurationSection("kills").getKeys(false)) {
			souls.add(s);
		}
		
		//getting page limit
		double limdub = (double) lim;
		double limit = Math.ceil((souls.size()/limdub));
		inst.debug("page limit = "+limit);
		int pagelim = (int) limit;
		if(page > pagelim) {
			page = pagelim;
		}
		
		int low = ((page*lim)-(lim))+1; 
		inst.debug("low = "+low);
		int max = page*lim;
		inst.debug("max = "+max);
		String header = "&7==========&8 [ &bPage &a"+page+"&7/&2"+pagelim+" &8] &7==========";
		String footer = "&7================================";
		
		if(max > souls.size()) {
			max = souls.size();
			if(max <= 8) {
				low = 1;
			} else {
				low = max - 7;
			}
		}
		
		inst.debug("= NEW VALUES =");
		inst.debug("low = "+low);
		inst.debug("max = "+max);
		
		
		p.sendMessage(color(header));
		int cur = low;
		int mob = cur-1;
		inst.debug("cur = "+cur);
		inst.debug("mob = "+mob);
		
		
		String mess = "";
		String ent = souls.get(cur);
		while(cur <= max) {
			ent = souls.get(mob);
			mess = "&b"+souls.get(mob)+" &7= &f"+pconfig.getInt("kills."+ent);
			p.sendMessage(color(mess));
			mob++;
			cur++;
		}
		p.sendMessage(color(footer));
	}
	
	private void showCost(Player p, int page) {
		//Getting the souls that we charge for
		List<String> costs = inst.getConfig().getStringList("record");
		//How many lines should we display? TODO make this configurable
		int lim = 8;
		
		//getting page limit
		double limdub = (double) lim;
		double limit = Math.ceil((costs.size()/limdub));
		inst.debug("page limit = "+limit);
		int pagelim = (int) limit;
		if(page > pagelim) {
			page = pagelim;
		}
		
		//setting the highs and low
		int low = ((page*lim)-(lim))+1; 
		int max = page*lim;
		
		//Making sure that the range is always accurate and is a real entry
		if(max > costs.size()) {
			max = costs.size();
			if(max <= 8) {
				low = 1;
			} 
		} //unlike list, we don't have to keep an accurate number of count vs list position
		low--;
		max--;
		
		//TODO make this configurable
		String header = "&7==========&8 [ &dCost to capture page: &a"+page+"&7/&2"+pagelim+" &8] &7==========";
		String mess = "";
		String footer = "&7=============================================";
		
		p.sendMessage(color(header));
		int cur = low;
		while(cur <= max) {
			//getting cost of current mob
			int cost = inst.getConfig().getInt("cost.default.price");
			if(inst.getConfig().contains("cost."+costs.get(cur))) {
				cost = inst.getConfig().getInt("cost."+costs.get(cur)+".price");
			}
			mess = "&b"+costs.get(cur)+" &7= "+cost;
			p.sendMessage(color(mess));
			cur++;
		}
		p.sendMessage(color(footer));
	}
	
	private String color(String s) {
		String mess = ChatColor.translateAlternateColorCodes('&', s);
		return mess;
	}
	
	
}
