package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class HoverGame extends WordGame {
	private final static String DELIMITER = "((?<=\\Q%1$s\\E)|(?=\\Q%1$s\\E))";
	private static final String PERMISSION_PLAY_TYPE = "permission.play.hover";
	private static final String PERMISSION_START_TYPE = "permission.start.hover";
	private final List<String> showedMessages = new ArrayList<>();
	
	public HoverGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		
		List<String> messages = ConfigManager.getMessages().getStringList("games.hover");
		for (String message : messages) {
			//Replace the variables with the correct values.
			message = message.replace("{plugin}", ConfigManager.getMessages().getString("variables.plugin")).replace("{amount}", "" + reward.getAmount()).replace("{reward}", reward.getReward());
			
			//Split the string just before and just after {word}
			String[] inProgress = message.split(String.format(DELIMITER, "{word}"));
			
			//Build the json message to show
			StringBuilder jsonMessage = new StringBuilder("[");
			for (int i = 0; i < inProgress.length; i++) {
				//Create the json syntax
				if (inProgress[i].equalsIgnoreCase("{word}")) {
					// syntax:   {"text":"config-word", "hoverEvent":{"action":"show_text", "value":"input-word"}}
					jsonMessage.append("{\"text\":\"").append(ConfigManager.getMessages().getString("variables.HOVER")).append("\", \"hoverEvent\":{\"action\":\"show_text\", \"value\":\"").append(wordToType).append("\"}}");
				} else {
					jsonMessage.append("{\"text\":\"").append(inProgress[i]).append("\"}");
				}
				
				//Is it necessary for a comma to be added?
				if (i + 1 < inProgress.length)
					jsonMessage.append(",");
			}
			//Finish the json message
			showedMessages.add(translateAlternateColorCodes('&', jsonMessage.append("]").toString()));
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
		for (String message : showedMessages) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				plugin.getCompatibility().sendJson(p, message);
			}
		}
	}
	
}
