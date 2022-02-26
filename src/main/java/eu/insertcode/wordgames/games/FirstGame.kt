package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;

public class FirstGame extends LongWordGame {
	
	public FirstGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		showedWord = wordToType;
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_FIRST;
	}
	
	@Override
	String getMessageConfigPath() {
		return "games.first";
	}
}
