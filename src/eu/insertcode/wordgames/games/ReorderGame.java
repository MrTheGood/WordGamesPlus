package eu.insertcode.wordgames.games;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;

public class ReorderGame extends LongWordGame {
	
	public ReorderGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		this.showedWord = reorderString(showedWord);
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_REORDER;
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
