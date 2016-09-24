package me.danielkinz.antiwither;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AntiWither extends JavaPlugin implements Listener {

	private static List<String> worlds = new ArrayList<String>();
	private static boolean filter = false; // true = whitelist

	public void onEnable() {
		if (!registerConfig()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		registerListeners();
	}

	public boolean registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();

		if (getConfig().getString("Filter").equalsIgnoreCase("blacklist")) {
			filter = false;
		} else if (getConfig().getString("Filter").equalsIgnoreCase("whitelist")) {
			filter = true;
		} else {
			getLogger().log(Level.SEVERE, "Invalid filter type. Disabling plugin...");
			return false;
		}

		worlds = getConfig().getStringList("Worlds");
		return true;
	}

	public void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
	}

	@EventHandler
	public void onWitherSpawn(CreatureSpawnEvent event) {
		if (event.getEntity().getType().equals(EntityType.WITHER)
				&& (filter ^ worlds.contains(event.getLocation().getWorld().getName()))) {
			event.setCancelled(true);
		}

	}
}
