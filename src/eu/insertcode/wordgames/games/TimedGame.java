package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

import eu.insertcode.wordgames.Main;

public class TimedGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.timed";
	private static final String PERMISSION_START_TYPE = "permission.start.timed";
	private final int seconds;
	private final ArrayList<Player> winners = new ArrayList<>();
	
	public TimedGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		this.seconds = plugin.getConfig().getInt("gameOptions.timed.secondsToType");
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (String message : Main.getColouredMessages("games.timed.stop"))
				Bukkit.broadcastMessage(message.replace("{seconds}", "" + seconds));
			endGame();
		}, seconds * 20);
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
	String getMessageConfigPath() {
		return "games.timed.start";
	}
	
	/**
	 * Sends a message only to the winner, and doesn't broadcast it.
	 */
	@Override
	void sendWinnerMessage(Player winner) {
		String[] messages = Main.getColouredMessages("games.timed.gameWon");
		for (String message : messages) {
			message = formatGameMessage(message, wordToType).replace("{player}", winner.getDisplayName());
			winner.sendMessage(message);
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
			
			if (e.getMessage().equalsIgnoreCase(wordToType)) {
				String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
				command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
				Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
				
				winners.add(p);
				sendWinnerMessage(p);
			}
		});
	}
}
