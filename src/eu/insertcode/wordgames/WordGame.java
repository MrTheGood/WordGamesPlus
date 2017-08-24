package eu.insertcode.wordgames;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Maarten de Goede - insertCode.eu
 * WordGames class
 */
public class WordGame {
	public static enum Type {HOVER, REORDER, UNMUTE};
	public Type wordGameType;	 	//WordGame type
	
	public int amount,		//The amount of the reward for the winner
			schedulerID;	//The ID of the scheduled task
	public String reward, 	//The reward for the winner
			showedWord, 	//The word which was shown in the chat
			wordToType;		//The correct word
	
	private ConfigurationManager configManager;
	private Main plugin;
	private Utils utils;
	public WordGame(Main instance) {
		plugin = instance;
		configManager = ConfigurationManager.instance;
		utils = plugin.utils;
	}
	
	
	/**
	 * Starts the autobroadcast.
	 */
	public void startAutoBroadcaster() {
		schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				//Send the message
				sendGameMessage();
			}
		}, 20 * 10, plugin.getConfig().getInt("gameOptions.scheduler.timerInSeconds") * 20);
	}
	/**
	 * Stops the autobroadcast.
	 */
	public void stopAutoBroadcaster() {
		Bukkit.getScheduler().cancelTask(schedulerID);
	}
	
	
	/**
	 * Sends the winner message
	 * @param winner
	 */
	public void sendWinnerMessage(Player winner) {
		//Get the messages.
		List<String> messages = configManager.getMessages().getStringList("games.gameWon");
		for (String message : messages) {
			//Replace the variables with the correct value.
			message = message.replace("{plugin}", configManager.getMessages().getString("variables.plugin")).replace("{player}", winner.getDisplayName())
					.replace("{word}", wordToType).replace("{aantal}", "" + amount).replace("{reward}", reward);
			//Broadcast the message.
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	
	/**
	 * Translates color codes, creates JSON syntax and sends the message.
	 */
	public void sendGameMessage() {
		List<String> messages;
		//Check the minigame type
		switch(wordGameType) {
		case HOVER:
			//The type is hover.
			//Get the messages.
			messages = configManager.getMessages().getStringList("games.hover");
			for (String message : messages) {
				//Replace the variables with the correct values.
				message = message.replace("{plugin}", configManager.getMessages().getString("variables.plugin")).replace("{amount}", "" + amount).replace("{reward}", reward);
				
				//If the message should be editted with JSON,
				if (message.contains("{word}")) {
					//Replace '{word}' in a way that it is easier to split. I know this looks stupid and it probably is but I don't care.
					message = message.replace("{word}", "eefec303079ad17405c889e092e105b0{word}eefec303079ad17405c889e092e105b0");
					//Split the message on the above defined locations
					String[] inProgress = message.split("eefec303079ad17405c889e092e105b0");
					
					
					//Create the string
					String jsonMessage = "[";
					
					//Loop through the inProgress array
					for (int i = 0; i < inProgress.length; i++) {
						
						//If the current value is where the hover should be,
						if (inProgress[i].equalsIgnoreCase("{word}")) {
							//Create the json syntax
							// syntax:   {"text":"config-word", "hoverEvent":{"action":"show_text", "value":"input-word"}}
							jsonMessage += "{\"text\":\"" + configManager.getMessages().getString("variables.HOVER") + "\", \"hoverEvent\":{\"action\":\"show_text\", \"value\":\"" + wordToType + "\"}}";
						} else {
							// Create the json syntax for just text.
							jsonMessage += "{\"text\":\"" + inProgress[i] + "\"}";
						}
						
						//Is it necessary for a comma to be added?
						if (i + 1 < inProgress.length) {
							jsonMessage += ",";
						}
					}
					//Finnish the json syntax stuff
					jsonMessage = translateAlternateColorCodes('&', jsonMessage) + "]";
					
					//Send the message to all players
					for (Player p : Bukkit.getOnlinePlayers()) {
						plugin.getCompatibility().sendJson(p, jsonMessage);
					}
				} else {
					//Just send the message.
					Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
				}
			}
			break;
		case REORDER:
			//The type is reorder.
			//Get the messages.
			messages = configManager.getMessages().getStringList("games.reorder");
			for (String message : messages) {
				//Replace the variables with the correct values.
				message = message.replace("{plugin}", configManager.getMessages().getString("variables.plugin")).replace("{word}", showedWord).replace("{amount}", "" + amount).replace("{reward}", reward);
				//Broadcast the message.
				Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
			}
			break;
		case UNMUTE:
			//The type is unmute.
			//Get the messages.
			messages = configManager.getMessages().getStringList("games.unmute");
			for (String message : messages) {
				//Replace the variables with the correct values.
				message = message.replace("{plugin}", configManager.getMessages().getString("variables.plugin")).replace("{word}", showedWord).replace("{amount}", "" + amount).replace("{reward}", reward);
				//Broadcast the message.
				Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
			}
			break;
		default:
			//Something went wrong
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.isOp()) {
					//warn the online OP's
					p.sendMessage(utils.getErrorMessage("error.somethingWrong"));
				}
			}
		}
	}
	

}
