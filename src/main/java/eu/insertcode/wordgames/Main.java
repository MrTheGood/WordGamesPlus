package eu.insertcode.wordgames;

import eu.insertcode.wordgames.games.WordGame;
import eu.insertcode.wordgames.util.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static eu.insertcode.wordgames.util.UpdateCheckerKt.checkUpdate;
import static org.bukkit.ChatColor.translateAlternateColorCodes;

/**
 * @author Maarten de Goede - mrthegood.dev
 * Main class
 */
public class Main extends JavaPlugin implements Listener {
	ArrayList<WordGame> wordGames = new ArrayList<>();

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

	void reload() {
		ConfigManager.reloadMessages();
		reloadConfig();
	}

	public void removeGame(WordGame game) {
		wordGames.remove(game);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		ConfigManager.createFiles(this);
		getCommand("wordgame").setExecutor(new CommandHandler(this));

		AutoStart.setPlugin(this);
		AutoStart.autoStart();
		checkUpdate(this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		for (WordGame game : wordGames) {
			game.onPlayerChat(e);
		}
	}
}
