package eu.insertcode.wordgames;

import eu.insertcode.wordgames.config.ConfigManager;
import eu.insertcode.wordgames.games.WordGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static eu.insertcode.wordgames.util.UpdateCheckerKt.checkUpdate;

/**
 * @author Maarten de Goede - mrthegood.dev
 * Main class
 */
public class Main extends JavaPlugin implements Listener {
	ArrayList<WordGame> wordGames = new ArrayList<>();

	public void removeGame(WordGame game) {
		wordGames.remove(game);
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);

		ConfigManager.INSTANCE.createFiles(this);
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
