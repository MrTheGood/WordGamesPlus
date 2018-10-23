package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class WordGame {
	private static final String PERMISSION_PLAY = "wordgamesplus.play";
	private static final String PERMISSION_START = "wordgamesplus.start";
	final Reward reward;        //The reward the player will get for winning.
	final Main plugin;
	String showedWord,    //The word which was shown in the chat
			wordToType;            //The correct word
	private int schedulerID;        //The ID of the scheduled task
	
	WordGame(Main instance, String wordToType, Reward reward) {
		plugin = instance;
		this.wordToType = wordToType;
		this.reward = reward;
		this.showedWord = wordToType;
		startAutoBroadcaster();
	}
	
	static boolean hasStartPermission(CommandSender s) {
		return s.hasPermission(PERMISSION_START);
	}
	
	public boolean hasPlayPermission(Player p) {
		return p.hasPermission(PERMISSION_PLAY);
	}
	
	private void startAutoBroadcaster() {
		//Send the message
		schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::sendGameMessage, 20 * 10, plugin.getConfig().getInt("gameOptions.scheduler.timerInSeconds") * 20);
	}
	
	public void stopAutoBroadcaster() {
		Bukkit.getScheduler().cancelTask(schedulerID);
	}
	
	void sendWinnerMessage(Player winner) {
		List<String> messages = ConfigManager.getMessages().getStringList("games.gameWon");
		for (String message : messages) {
			message = formatGameMessage(message, wordToType).replace("{player}", winner.getDisplayName());
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	String formatGameMessage(String message, String word) {
		return message.replace("{plugin}", ConfigManager.getMessages().getString("variables.plugin"))
				.replace("{word}", word)
				.replace("{amount}", "" + reward.getAmount())
				.replace("{reward}", reward.getReward());
	}
	
	void sendGameMessage() {
		List<String> messages = ConfigManager.getMessages().getStringList("games.timed.start");
		for (String message : messages) {
			message = formatGameMessage(message, showedWord);
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			Player p = e.getPlayer();
			//If the player types the correct word.
			if (e.getMessage().equalsIgnoreCase(wordToType)) {
				String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
				command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				sendWinnerMessage(p);
				stopAutoBroadcaster();
				plugin.removeGame(this);
			}
		});
	}
	
	public static class Reward {
		private final int amount;        //The amount of the reward for the winner
		private final String reward;    //The reward for the winner
		
		public Reward(int amount, String reward) {
			this.amount = amount;
			this.reward = reward;
		}
		
		int getAmount() {
			return amount;
		}
		
		String getReward() {
			return reward;
		}
	}
}
