package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;

public class CalculateGame extends LongWordGame {
	public CalculateGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		int numberOne = (int) (Math.random() * 51);
		int numberTwo = (int) (Math.random() * 20) + 1;
		switch ((int) (Math.random() * 4)) {
			case 1:
				this.wordToType = Math.round((double) numberOne + numberTwo) + "";
				showedWord = numberOne + " + " + numberTwo;
				break;
			case 2:
				this.wordToType = Math.round((double) numberOne - numberTwo) + "";
				showedWord = numberOne + " - " + numberTwo;
				break;
			case 3:
				this.wordToType = Math.round((double) numberOne * numberTwo) + "";
				showedWord = numberOne + " * " + numberTwo;
				break;
			default:
				this.wordToType = Math.round((double) numberOne / numberTwo) + "";
				showedWord = numberOne + " / " + numberTwo;
		}
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_CALCULATE;
	}
	
	@Override
	String getMessageConfigPath() {
		return "games.calculate";
	}
}
