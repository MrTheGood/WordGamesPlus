package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.utils.ConfigManager;
import eu.insertcode.wordgames.utils.Utils;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class TimedGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.timed";
	private static final String PERMISSION_START_TYPE = "permission.start.timed";
	private int seconds = 0;
	
	public TimedGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		final TimedGame game = this;
		final Main plugin = this.plugin;
		
		this.seconds = plugin.getConfig().getInt("gameOptions.timed.secondsToType");
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (String message : Utils.getColouredMessages("games.timed.stop"))
				Bukkit.broadcastMessage(message);
			game.stopAutoBroadcaster();
			plugin.removeGame(game);
		}, seconds * 20);
	}
	
	public static boolean hasStartPermission(CommandSender s) {
		return WordGame.hasStartPermission(s) || s.hasPermission(PERMISSION_START_TYPE);
	}
	
	@Override
	public boolean hasPlayPermission(Player p) {
		return super.hasPlayPermission(p) || p.hasPermission(PERMISSION_PLAY_TYPE);
	}
	
	@Override
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
	
	@Override
	public void sendGameMessage() {
		//The type is reorder.
		//Get the messages.
		List<String> messages = ConfigManager.getMessages().getStringList("games.timed.start");
		for (String message : messages) {
			//Replace the variables with the correct values.
			message = message.replace("{plugin}", ConfigManager.getMessages().getString("variables.plugin"))
					.replace("{word}", showedWord)
					.replace("{amount}", "" + reward.getAmount())
					.replace("{reward}", reward.getReward())
					.replace("{seconds}", "" + seconds);
			//Broadcast the message.
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	@Override
	public boolean checkMessage(String message, Player p) {
		//If the player types the correct word.
		if (message.equalsIgnoreCase(wordToType)) {
			String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
			command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
			Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
			sendWinnerMessage(p);
			return true;
		}
		return false;
	}
}
