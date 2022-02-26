package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;
import eu.insertcode.wordgames.config.Config;
import eu.insertcode.wordgames.config.Messages;
import eu.insertcode.wordgames.message.MessageHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class TimedGame extends WordGame {
	private final int seconds;
	private final ArrayList<Player> winners = new ArrayList<>();
	
	public TimedGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);

		this.seconds = Config.GameOptions.Timed.INSTANCE.getSecondsToType();
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (String message : MessageHandler.INSTANCE.getMessages(Messages.Games.Timed.stop)) {
				message = formatGameMessage(message, wordToType);
				Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
			}
			endGame();
		}, seconds * 20);
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_TIMED;
	}
	
	
	@Override
	String getMessageConfigPath() {
		return Messages.Games.Timed.start;
	}
	
	@Override
	String formatGameMessage(String message, String word) {
		return super.formatGameMessage(message, word)
				.replace("{seconds}", "" + seconds);
	}
	
	/**
	 * Sends a message only to the winner, and doesn't broadcast it.
	 */
	@Override
	void sendWinnerMessage(Player winner) {
		List<String> messages = MessageHandler.INSTANCE.getMessages(Messages.Games.Timed.gameWon);
		for (String message : messages) {
			message = formatGameMessage(message, wordToType).replace("{player}", winner.getDisplayName());
			winner.sendMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	/**
	 * Doesn't stop the game when somebody wins.
	 * Also prevents the same player from winning twice.
	 */
	@Override
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Bukkit.getScheduler().runTask(plugin, () -> {
			Player p = e.getPlayer();
			if (winners.contains(p)) return;
			
			String message = ChatColor.stripColor(e.getMessage()).trim();
			if (message.equalsIgnoreCase(wordToType)) {
				if (!Permission.PLAY_ALL.forPlayer(p, getPlayPermission())) {
					MessageHandler.INSTANCE.sendMessage(p, Messages.Error.noPlayPermissions, true);
					return;
				}

				String command = Config.GameOptions.INSTANCE.getRewardCommandSyntax();
				command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				
				winners.add(p);
				sendWinnerMessage(p);
			}
		});
	}
}
