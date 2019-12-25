package eu.insertcode.wordgames.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ConfigManager {
	
	private static File messagesFile;
	private static FileConfiguration messages;
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void createFiles(JavaPlugin p) {
		File configFile = new File(p.getDataFolder(), "config.yml");
		messagesFile = new File(p.getDataFolder(), "messages.yml");
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			p.saveResource("config.yml", false);
			Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] config.yml not found. Creating...");
		}
		if (!messagesFile.exists()) {
			messagesFile.getParentFile().mkdirs();
			p.saveResource("messages.yml", false);
			Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] messages.yml not found. Creating...");
		}
		
		FileConfiguration config = new YamlConfiguration();
		messages = new YamlConfiguration();
		try {
			config.load(configFile);
			messages.load(messagesFile);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] Something went wrong while loading the config.");
		} catch (InvalidConfigurationException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] The config is wrong.");
		}
	}
	
	
	public static FileConfiguration getMessages() {
		return messages;
	}
	
	public static void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}
	
}
