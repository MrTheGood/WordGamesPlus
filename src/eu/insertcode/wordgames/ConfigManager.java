package eu.insertcode.wordgames;

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
	static void createFiles(JavaPlugin p) {
		File configFile = new File(p.getDataFolder(), "config.yml");
		messagesFile = new File(p.getDataFolder(), "messages.yml");
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			p.saveResource("config.yml", false);
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] config.yml not found. Creating...");
		}
		if (!messagesFile.exists()) {
			messagesFile.getParentFile().mkdirs();
			p.saveResource("messages.yml", false);
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] messages.yml not found. Creating...");
		}
		
		FileConfiguration config = new YamlConfiguration();
		messages = new YamlConfiguration();
		try {
			config.load(configFile);
			messages.load(messagesFile);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] Something went wrong while loading the config.");
		} catch (InvalidConfigurationException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] The config is wrong.");
		}
	}
	
	
	public static FileConfiguration getMessages() {
		return messages;
	}
	
	static void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}
	
}
