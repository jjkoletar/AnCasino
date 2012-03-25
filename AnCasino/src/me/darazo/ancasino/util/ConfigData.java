package me.darazo.ancasino.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import me.darazo.ancasino.AnCasino;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigData {

	protected AnCasino plugin;

	public FileConfiguration config;
	public FileConfiguration slots;
	public FileConfiguration stats;
	public File slotsFile;
	public File statsFile;

	public String prefixColor, chatColor;
	public Boolean displayPrefix, trackStats, allowDiagonals, protection;

	// Initialize ConfigData
	public ConfigData(AnCasino plugin) {
		this.plugin = plugin;
	}

	// Load all config data
	public void load() {

		config = plugin.getConfig();
		config.options().copyDefaults(true);
		plugin.saveConfig();

		setGlobals();

		statsFile = new File(plugin.getDataFolder(), "stats.yml");
		stats = YamlConfiguration.loadConfiguration(statsFile);

		slotsFile = new File(plugin.getDataFolder(), "slots.yml");
		slots = YamlConfiguration.loadConfiguration(slotsFile);

		plugin.slotData.loadSlots();
		plugin.typeData.loadTypes();
		plugin.statsData.loadStats();

	}

	// Save all config data
	public void save() {

		plugin.saveConfig();
		saveSlots();
		saveStats();

	}

	// Set up global settings
	private void setGlobals() {

		this.prefixColor = config.getString("options.chat.plugin-prefix-color",
				"&c");
		this.chatColor = config.getString("options.chat.chat-color", "&a");
		this.displayPrefix = config.getBoolean(
				"options.chat.display-plugin-prefix", true);
		this.trackStats = config.getBoolean("options.track-statistics", true);
		this.allowDiagonals = config.getBoolean(
				"options.allow-diagonal-winnings", true);
		this.protection = config.getBoolean("options.enable-slot-protection",
				true);
	}

	// Save slots data
	public void saveSlots() {
		try {
			slots.save(slotsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Save stats data
	public void saveStats() {

		Collection<Stat> stats = plugin.statsData.getStats();
		if (stats != null && !stats.isEmpty()) {
			for (Stat stat : stats) {
				String path = "types." + stat.getType() + ".";
				this.stats.set(path + "spins", stat.getSpins());
				this.stats.set(path + "won", stat.getLost());
				this.stats.set(path + "lost", stat.getLost());
			}
		}

		this.stats.set("global.spins", plugin.statsData.globalSpins);
		this.stats.set("global.won", plugin.statsData.globalWon);
		this.stats.set("global.lost", plugin.statsData.globalLost);

		try {
			this.stats.save(statsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Set default values for new type
	public void setTypeDefaults(String name) {

		config.set("types." + name + ".price", 100);
		config.set("types." + name + ".permission", 0);
		config.set("types." + name + ".price", 100);

		ArrayList<String> reel = new ArrayList<String>();
		reel.add("42,10");
		reel.add("41,5");
		reel.add("57,2");
		config.set("types." + name + ".reel", reel);

		config.set("types." + name + ".rewards.42.message", "Winner");
		config.set("types." + name + ".rewards.42.money", 100);
		config.set("types." + name + ".rewards.41.message", "Winner");
		config.set("types." + name + ".rewards.41.money", 100);
		config.set("types." + name + ".rewards.57.message", "Winner");
		config.set("types." + name + ".rewards.57.money", 100);

		config.set("types." + name + ".messages.insufficient-funds",
				"Insufficient funds.");
		config.set("types." + name + ".messages.in-use", "In use.");
		config.set("types." + name + ".messages.no-win", "No win.");
		config.set("types." + name + ".messages.start", "Start.");
		config.set("types." + name + ".messages.help", new ArrayList<String>());

		plugin.saveConfig();
	}

}
