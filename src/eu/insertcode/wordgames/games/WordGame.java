package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.utils.ConfigManager;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public abstract class WordGame {
	private static final String PERMISSION_PLAY = "wordgamesplus.play";
	private static final String PERMISSION_START = "wordgamesplus.start";
	protected Reward reward;        //The reward the player will get for winning.
	String showedWord,    //The word which was shown in the chat
			wordToType;            //The correct word
	private int schedulerID;        //The ID of the scheduled task
	protected Main plugin;
	
	WordGame(Main instance, String wordToType, Reward reward) {
		plugin = instance;
		this.wordToType = wordToType;
		this.reward = reward;
		sendGameMessage();
		startAutoBroadcaster();
	}
	
	static boolean hasStartPermission(CommandSender s) {
		return s.hasPermission(PERMISSION_START);
	}
	
	public boolean hasPlayPermission(Player p) {
		return p.hasPermission(PERMISSION_PLAY);
	}
	
	/**
	 * Starts the autobroadcast.
	 */
	private void startAutoBroadcaster() {
		//Send the message
		schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::sendGameMessage, 20 * 10, plugin.getConfig().getInt("gameOptions.scheduler.timerInSeconds") * 20);
	}
	
	/**
	 * Stops the autobroadcast.
	 */
	public void stopAutoBroadcaster() {
		Bukkit.getScheduler().cancelTask(schedulerID);
	}
	
	/**
	 * Sends the winner message
	 *
	 * @param winner the winner
	 */
	public void sendWinnerMessage(Player winner) {
		//Get the messages.
		List<String> messages = ConfigManager.getMessages().getStringList("games.gameWon");
		for (String message : messages) {
			//Replace the variables with the correct value.
			message = message.replace("{plugin}", ConfigManager.getMessages().getString("variables.plugin")).replace("{player}", winner.getDisplayName())
					.replace("{word}", wordToType).replace("{amount}", "" + reward.getAmount()).replace("{reward}", reward.getReward());
			//Broadcast the message.
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	/**
	 * Translates color codes, creates JSON syntax and sends the message.
	 */
	public abstract void sendGameMessage();
	
	public boolean checkMessage(String message, Player p) {
		//If the player types the correct word.
		if (message.equalsIgnoreCase(wordToType)) {
			String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
			command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
			Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
			sendWinnerMessage(p);
			stopAutoBroadcaster();
			return true;
		}
		return false;
	}
	
	public static class Reward {
		private final int amount;        //The amount of the reward for the winner
		private final String reward;    //The reward for the winner
		
		public Reward(int amount, String reward) {
			this.amount = amount;
			this.reward = reward;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public String getReward() {
			return reward;
		}
	}
}
