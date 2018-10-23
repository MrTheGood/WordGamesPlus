package eu.insertcode.wordgames.games;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.insertcode.wordgames.Main;

public class CalculateGame extends LongWordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.calculate";
	private static final String PERMISSION_START_TYPE = "permission.start.calculate";
	
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
	
	public static boolean hasStartPermission(CommandSender s) {
		return WordGame.hasStartPermission(s) || s.hasPermission(PERMISSION_START_TYPE);
	}
	
	@Override
	public boolean hasPlayPermission(Player p) {
		return super.hasPlayPermission(p) || p.hasPermission(PERMISSION_PLAY_TYPE);
	}
	
	@Override
	String getMessageConfigPath() {
		return "games.calculate";
	}
}
