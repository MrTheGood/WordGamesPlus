package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class HoverGame extends LongWordGame {
	private final static String DELIMITER = "((?<=\\Q%1$s\\E)|(?=\\Q%1$s\\E))";
	private final List<String> showedMessages = new ArrayList<>();
	
	public HoverGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		String[] messages = Main.getMessages("games.hover");
		for (String message : messages) {
			message = message
					.replace("{amount}", "" + reward.getAmount())
					.replace("{reward}", reward.getReward());
			
			//Split the string just before and just after {word}
			String[] inProgress = message.split(String.format(DELIMITER, "{word}"));
			
			StringBuilder jsonMessage = new StringBuilder("[");
			for (int i = 0; i < inProgress.length; i++) {
				if (inProgress[i].equalsIgnoreCase("{word}")) {
					// syntax:   {"text":"config-word", "hoverEvent":{"action":"show_text", "value":"input-word"}}
					jsonMessage.append("{\"text\":\"").append(ConfigManager.getMessages().getString("variables.HOVER")).append("\", \"hoverEvent\":{\"action\":\"show_text\", \"value\":\"").append(wordToType).append("\"}}");
				} else {
					jsonMessage.append("{\"text\":\"").append(inProgress[i]).append("\"}");
				}
				
				if (i + 1 < inProgress.length)
					jsonMessage.append(",");
			}
			//Finish the json message
			showedMessages.add(translateAlternateColorCodes('&', jsonMessage.append("]").toString()));
		}
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_HOVER;
	}
	
	@Override
	String getMessageConfigPath() {
		return null;
	}
	
	@Override
	void sendGameMessage() {
		for (String message : showedMessages) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.getCompatibility().sendJson(p, message);
			}
		}
	}
	
}
