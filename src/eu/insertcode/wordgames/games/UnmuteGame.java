package eu.insertcode.wordgames.games;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.List;

import org.bukkit.Bukkit;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.utils.ConfigManager;
import eu.insertcode.wordgames.utils.Utils;

public class UnmuteGame extends WordGame {
	
	public UnmuteGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		showedWord = Utils.muteString(wordToType,
				plugin.getConfig().getDouble("gameOptions.unmute.percentageOfCharactersToMute"));
	}
	
	@Override
	public void sendGameMessage() {
		// The type is unmute.
		// Get the messages.
		List<String> messages = ConfigManager.getMessages().getStringList("games.unmute");
		for (String message : messages) {
			// Replace the variables with the correct values.
			message = message.replace("{plugin}", ConfigManager.getMessages().getString("variables.plugin"))
					.replace("{word}", showedWord).replace("{amount}", "" + reward.getAmount()).replace("{reward}", reward.getReward());
			// Broadcast the message.
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
}
