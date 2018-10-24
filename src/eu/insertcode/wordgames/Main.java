package eu.insertcode.wordgames;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import eu.insertcode.wordgames.compatibility.Compatibility;
import eu.insertcode.wordgames.compatibility.Compatibility_1_10_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_11_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_12_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_13_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_13_R2;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R2;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R3;
import eu.insertcode.wordgames.compatibility.Compatibility_1_9_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_9_R2;
import eu.insertcode.wordgames.games.WordGame;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * @author Maarten de Goede - insertCode.eu
 * Main class
 */
public class Main extends JavaPlugin implements Listener {
	ArrayList<WordGame> wordGames = new ArrayList<>();
	private Compatibility compatibility;
	
	/**
	 * Gets a message from the config and puts it in an array.
	 * @param path The path to the message.
	 * @return A coloured String array.
	 */
	public static String[] getMessages(String path) {
		FileConfiguration msgConfig = ConfigManager.getMessages();
		String[] messages;
		messages = ConfigManager.getMessages().isList(path)
				? msgConfig.getStringList(path).toArray(new String[0]) : new String[]{msgConfig.getString(path)};
		
		for (int i = 0; i < messages.length; i++) {
			messages[i] = messages[i].replace("{plugin}", msgConfig.getString("variables.plugin"));
		}
		
		return messages;
	}
	
	public static String[] getColouredMessages(String path) {
		String[] messages = getMessages(path);
		for (int i = 0; i < messages.length; i++) {
			messages[i] = translateAlternateColorCodes('&', messages[i]);
		}
		
		return messages;
	}
	
	private boolean setup() {
		String version;
		
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		getLogger().info("[WordGames+, insertCode] Your server is running " + version);
		
		switch (version) {
			case "v1_13_R2":
				compatibility = new Compatibility_1_13_R2();
				return true;
			case "v1_13_R1":
				compatibility = new Compatibility_1_13_R1();
				return true;
			case "v1_12_R1":
				compatibility = new Compatibility_1_12_R1();
				return true;
			case "v1_11_R1":
				compatibility = new Compatibility_1_11_R1();
				return true;
			case "v1_10_R1":
				compatibility = new Compatibility_1_10_R1();
				return true;
			case "v1_9_R2":
				compatibility = new Compatibility_1_9_R2();
				return true;
			case "v1_9_R1":
				compatibility = new Compatibility_1_9_R1();
				return true;
			case "v1_8_R3":
				compatibility = new Compatibility_1_8_R3();
				return true;
			case "v1_8_R2":
				compatibility = new Compatibility_1_8_R2();
				return true;
			case "v1_8_R1":
				compatibility = new Compatibility_1_8_R1();
				return true;
			default:
				return false;
		}
	}
	
	void reload() {
		ConfigManager.reloadMessages();
		reloadConfig();
	}
	
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	public void removeGame(WordGame game) {
		wordGames.remove(game);
	}
	
	@Override
	public void onEnable() {
		if (setup()) {
			// Register the plugin events in this class
			getServer().getPluginManager().registerEvents(this, this);
			
			ConfigManager.createFiles(this);
			getCommand("wordgame").setExecutor(new CommandHandler(this));
			
			AutoStart.setPlugin(this);
			AutoStart.autoStart(); // Start the autoStart scheduler.
		} else {
			getLogger().severe("Failed to setup WordGames+!");
			getLogger().severe("Your server version is not compatible with this plugin!");
			
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		for (WordGame game : wordGames) {
			if (Permission.PLAY_ALL.forPlayer(p, game.getPlayPermission()))
				game.onPlayerChat(e);
			else for (String message : getColouredMessages("error.noPlayPermissions"))
				p.sendMessage(message);
		}
	}
}
