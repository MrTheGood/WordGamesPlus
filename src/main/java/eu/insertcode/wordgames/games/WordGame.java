package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public abstract class WordGame {
	final Reward reward;
	final Main plugin;
	String showedWord,
			wordToType;
	
	WordGame(Main instance, String wordToType, Reward reward) {
		plugin = instance;
		this.wordToType = wordToType;
		this.reward = reward;
		this.showedWord = wordToType;
	}
	
	public void endGame() {
		plugin.removeGame(this);
	}
	
	void sendWinnerMessage(Player winner) {
		String[] messages = Main.getMessages("games.gameWon");
		for (String message : messages) {
			message = formatGameMessage(message, wordToType).replace("{player}", winner.getName());
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	public abstract Permission getPlayPermission();
	
	abstract String getMessageConfigPath();
	
	public String[] getGameMessages() {
		String[] messages = Main.getMessages(getMessageConfigPath());
		for (int i = 0; i < messages.length; i++) {
			messages[i] = formatGameMessage(messages[i], showedWord);
			messages[i] = translateAlternateColorCodes('&', messages[i]);
			
		}
		return messages;
	}
	
	void sendGameMessage() {
		String[] messages = getGameMessages();
		for (String message : messages) {
			Bukkit.broadcastMessage(message);
		}
	}
	
	String formatGameMessage(String message, String word) {
		return message.replace("{word}", word)
				.replace("{amount}", "" + reward.getAmount())
				.replace("{reward}", reward.getReward());
	}
	
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			Player p = e.getPlayer();
			//If the player types the correct word.
			String message = ChatColor.stripColor(e.getMessage()).trim();
			if (message.equalsIgnoreCase(wordToType)) {
				if (!Permission.PLAY_ALL.forPlayer(p, getPlayPermission())) {
					for (String msg : Main.getColouredMessages("error.noPlayPermissions"))
						p.sendMessage(msg);
					return;
				}
				
				String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
				command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				
				sendWinnerMessage(p);
				endGame();
			}
		});
	}
	
	public static class Reward {
		private final int amount;
		private final String reward;
		
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
