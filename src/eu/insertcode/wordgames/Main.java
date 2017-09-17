package eu.insertcode.wordgames;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import eu.insertcode.wordgames.compatibility.Compatibility;
import eu.insertcode.wordgames.compatibility.Compatibility_1_10_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_11_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_12_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R2;
import eu.insertcode.wordgames.compatibility.Compatibility_1_8_R3;
import eu.insertcode.wordgames.compatibility.Compatibility_1_9_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_9_R2;
import eu.insertcode.wordgames.games.WordGame;
import eu.insertcode.wordgames.utils.ConfigManager;
import eu.insertcode.wordgames.utils.WordGameUtils;

/**
 * @author Maarten de Goede - insertCode.eu Main class
 */
public class Main extends JavaPlugin implements Listener {
	public ConfigManager configManager;
	private Compatibility compatibility;
	
	public ArrayList<WordGame> wordGames = new ArrayList<>();
	
	@Override
	public void onEnable() {
		if (setup()) {
			// Register the plugin events in this class
			getServer().getPluginManager().registerEvents(this, this);
			
			ConfigManager.createFiles(this);
			getCommand("wordgame").setExecutor(new CommandHandler(this));

			WordGameUtils.setPlugin(this);
			WordGameUtils.autoStart(); // Start the autoStart scheduler.
		} else {
			getLogger().severe("Failed to setup WordGames+!");
			getLogger().severe("Your server version is not compatible with this plugin!");
			
			Bukkit.getPluginManager().disablePlugin(this);
		}
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
			// TODO: Original used .equals(). Does this still work?
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
	
	public void reload() {
		ConfigManager.reloadMessages();
		reloadConfig();
	}
	
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		// For all games
		for (int i = 0; i < wordGames.size(); i++) {
			WordGame wg = wordGames.get(i);
			if (wg.checkMessage(e.getMessage(), e.getPlayer()))
				wordGames.remove(i);
		}
	}
	
}
