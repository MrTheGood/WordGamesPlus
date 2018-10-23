package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import eu.insertcode.wordgames.Main;

public abstract class WordGame {
	private static final String PERMISSION_PLAY = "wordgamesplus.play";
	private static final String PERMISSION_START = "wordgamesplus.start";
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
	
	static boolean hasStartPermission(CommandSender s) {
		return s.hasPermission(PERMISSION_START);
	}
	
	public boolean hasPlayPermission(Player p) {
		return p.hasPermission(PERMISSION_PLAY);
	}
	
	
	public void endGame() {
		plugin.removeGame(this);
	}
	
	void sendWinnerMessage(Player winner) {
		String[] messages = Main.getColouredMessages("games.gameWon");
		for (String message : messages) {
			message = formatGameMessage(message, wordToType).replace("{player}", winner.getDisplayName());
			Bukkit.broadcastMessage(message);
		}
	}
	
	abstract String getMessageConfigPath();
	
	void sendGameMessage() {
		String[] messages = Main.getColouredMessages(getMessageConfigPath());
		for (String message : messages)
			Bukkit.broadcastMessage(formatGameMessage(message, showedWord));
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
			if (e.getMessage().equalsIgnoreCase(wordToType)) {
				
				String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
				command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				
				sendWinnerMessage(p);
				endGame();
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
