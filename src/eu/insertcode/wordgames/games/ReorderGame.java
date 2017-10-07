package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class ReorderGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.reorder";
	private static final String PERMISSION_START_TYPE = "permission.start.reorder";
	
	public ReorderGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		this.showedWord = reorderString(showedWord);
		sendGameMessage();
	}
	
	public static boolean hasStartPermission(CommandSender s) {
		return WordGame.hasStartPermission(s) || s.hasPermission(PERMISSION_START_TYPE);
	}
	
	@Override
	public boolean hasPlayPermission(Player p) {
		return super.hasPlayPermission(p) || p.hasPermission(PERMISSION_PLAY_TYPE);
	}
	
	@Override
	void sendGameMessage() {
		List<String> messages = ConfigManager.getMessages().getStringList("games.reorder");
		for (String message : messages) {
			message = formatGameMessage(message);
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	
	private String reorderString(String string) {
		// Reorder the input.
		List<Character> characters = new ArrayList<>();
		for (char c : string.toCharArray()) {
			characters.add(c);
		}
		StringBuilder reordered = new StringBuilder(string.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			reordered.append(characters.remove(randPicker));
		}
		
		return "" + reordered;
	}
	
}
