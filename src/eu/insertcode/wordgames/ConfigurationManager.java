package eu.insertcode.wordgames;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationManager {
	public static ConfigurationManager instance;
	
	public ConfigurationManager() {
		instance = this;
	}
	
	private File configFile, messagesFile/*, dataFile*/;
	private FileConfiguration config, messages/*, data*/;
	
	public void createFiles(JavaPlugin p) {
		configFile = new File(p.getDataFolder(), "config.yml");
		messagesFile = new File(p.getDataFolder(), "messages.yml");
//		dataFile = new File(p.getDataFolder(), "data.yml");
		
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
//		if (!dataFile.exists()) {
//			dataFile.getParentFile().mkdirs();
//			p.saveResource("data.yml", false);
//			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] data.yml not found. Creating...");
//		}
		
		config = new YamlConfiguration();
		messages = new YamlConfiguration();
//		data = new YamlConfiguration();
		try {
			config.load(configFile);
			messages.load(messagesFile);
//			data.load(dataFile);
		} catch (IOException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] Something went wrong while loading the config.");
		} catch (InvalidConfigurationException e) {
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] The config is wrong.");
		}
	}
	
	
//	public FileConfiguration getData() {
//		return data;
//	}
//	public void saveData() {
//		try {
//			data.save(dataFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//	public void reloadData() {
//		data = YamlConfiguration.loadConfiguration(dataFile);
//	}
	
	
	public FileConfiguration getMessages() {
		return messages;
	}
	public void saveMessages() {
		try {
			messages.save(messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void reloadMessages() {
		messages = YamlConfiguration.loadConfiguration(messagesFile);
	}

}
