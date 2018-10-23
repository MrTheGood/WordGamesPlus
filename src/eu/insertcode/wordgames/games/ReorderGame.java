package eu.insertcode.wordgames.games;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.Main;

public class ReorderGame extends LongWordGame {
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
	String getMessageConfigPath() {
		return "games.reorder";
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
