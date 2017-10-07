package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class CalculateGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.calculate";
	private static final String PERMISSION_START_TYPE = "permission.start.calculate";
	
	public CalculateGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		int numberOne = (int) (Math.random() * 51);
		int numberTwo = (int) (Math.random() * 20) + 1;
		switch ((int) (Math.random() * 4)) {
			case 1:
				this.wordToType = round((double) numberOne + numberTwo, 2) + "";
				showedWord = numberOne + " + " + numberTwo;
				break;
			case 2:
				this.wordToType = round((double) numberOne - numberTwo, 2) + "";
				showedWord = numberOne + " - " + numberTwo;
				break;
			case 3:
				this.wordToType = round((double) numberOne * numberTwo, 2) + "";
				showedWord = numberOne + " * " + numberTwo;
				break;
			default:
				this.wordToType = round((double) numberOne / numberTwo, 2) + "";
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
	void sendGameMessage() {
		List<String> messages = ConfigManager.getMessages().getStringList("games.calculate");
		for (String message : messages) {
			message = formatGameMessage(message, showedWord);
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	private double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();
		
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
