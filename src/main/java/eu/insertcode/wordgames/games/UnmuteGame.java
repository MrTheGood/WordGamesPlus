package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;
import eu.insertcode.wordgames.config.Config;
import eu.insertcode.wordgames.config.Messages;

import java.util.ArrayList;
import java.util.List;

public class UnmuteGame extends LongWordGame {
	
	public UnmuteGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);

		double charactersToMute = Config.GameOptions.Unmute.INSTANCE.getPercentageOfCharactersToMute();
		showedWord = muteString(wordToType, charactersToMute);
		
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_UNMUTE;
	}
	
	@Override
	String getMessageConfigPath() {
		return Messages.Games.unmute;
	}
	
	private static int countAsterisks(String text) {
		int amount = 0;
		int position = text.indexOf('*');
		while (position >= 0) {
			amount++;
			position = text.indexOf('*', position + 1);
		}
		return amount;
	}
	
	private String muteString(String string, Double percentage) {
		List<Character> characters = new ArrayList<>();
		for (char c : string.toCharArray()) {
			characters.add(c);
		}
		// Calculate how many characters should be muted.
		int charactersToMute = (int) Math.floor(((double) string.length() / 100) * percentage) + 1;
		
		// Create the string
		StringBuilder muted = new StringBuilder();
		for (char ch : characters) {
			muted.append(ch);
		}
		while (countAsterisks(muted.toString()) < charactersToMute) {
			// Calculate which letter to replace.
			int randomChar = (int) Math.floor(Math.random() * string.length());
			characters.set(randomChar, '*');
			
			// Create the new string
			muted.delete(0, muted.length());
			for (char ch : characters) {
				muted.append(ch);
			}
		}
		
		return "" + muted;
	}
	
}
