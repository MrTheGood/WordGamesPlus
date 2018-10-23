package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class UnmuteGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.unmute";
	private static final String PERMISSION_START_TYPE = "permission.start.unmute";
	
	public UnmuteGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		showedWord = muteString(wordToType,
				plugin.getConfig().getDouble("gameOptions.unmute.percentageOfCharactersToMute"));
		sendGameMessage();
	}
	
	public static boolean hasStartPermission(CommandSender s) {
		return WordGame.hasStartPermission(s) || s.hasPermission(PERMISSION_START_TYPE);
	}
	
	@Override
	public boolean hasPlayPermission(Player p) {
		return super.hasPlayPermission(p) || p.hasPermission(PERMISSION_PLAY_TYPE);
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
	
	@Override
	void sendGameMessage() {
		List<String> messages = ConfigManager.getMessages().getStringList("games.unmute");
		for (String message : messages) {
			message = formatGameMessage(message, showedWord);
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
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
