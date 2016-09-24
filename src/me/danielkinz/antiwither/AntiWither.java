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

/**
 * Anti wither plugin
 * @author Danielkinz
 */
public class AntiWither extends JavaPlugin implements Listener {

	private static List<String> worlds = new ArrayList<String>();
	private static boolean filter = false; // true = whitelist

	/**
	 * Happens when the player is enabled
	 */
	public void onEnable() {
		if (!registerConfig()) {
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		registerListeners();
	}

	/**
	 * Reads the config.yml file and loads the settings
	 * @return
	 */
	public boolean registerConfig() {
		// Copies defaults
		getConfig().options().copyDefaults(true);
		saveConfig();

		// Checks the type of filter
		if (getConfig().getString("Filter").equalsIgnoreCase("blacklist")) {
			filter = false;
		} else if (getConfig().getString("Filter").equalsIgnoreCase("whitelist")) {
			filter = true;
		} else {
			// Invalid filter type
			getLogger().log(Level.SEVERE, "Invalid filter type. Disabling plugin...");
			return false;
		}

		// Reads the list of world names
		worlds = getConfig().getStringList("Worlds");
		return true;
	}

	/**
	 * Registers the wither spawn listener
	 */
	public void registerListeners() {
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(this, this);
	}

	/**
	 * A listener that is triggered when an entity is spawned
	 * @param event
	 */
	@EventHandler
	public void onWitherSpawn(CreatureSpawnEvent event) {
		// If the entity is a wither and the world matches the filter
		if (event.getEntity().getType().equals(EntityType.WITHER)
				&& (filter ^ worlds.contains(event.getLocation().getWorld().getName()))) {
			event.setCancelled(true);
		}

	}
}
