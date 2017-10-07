package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

import eu.insertcode.wordgames.ConfigManager;
import eu.insertcode.wordgames.Main;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class TimedGame extends WordGame {
	private static final String PERMISSION_PLAY_TYPE = "permission.play.timed";
	private static final String PERMISSION_START_TYPE = "permission.start.timed";
	private int seconds = 0;
	private ArrayList<Player> winners = new ArrayList<>();
	
	public TimedGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		final TimedGame game = this;
		final Main plugin = this.plugin;
		
		this.seconds = plugin.getConfig().getInt("gameOptions.timed.secondsToType");
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			for (String message : Main.getColouredMessages("games.timed.stop"))
				Bukkit.broadcastMessage(message.replace("{seconds}", "" + seconds));
			game.stopAutoBroadcaster();
			plugin.removeGame(game);
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
		//Sends a different message to the winner.
	void sendWinnerMessage(Player winner) {
		List<String> messages = ConfigManager.getMessages().getStringList("games.timed.gameWon");
		for (String message : messages) {
			message = formatGameMessage(message).replace("{player}", winner.getDisplayName());
			winner.sendMessage(message);
		}
	}
	
	@Override
	void sendGameMessage() {
		List<String> messages = ConfigManager.getMessages().getStringList("games.timed.start");
		for (String message : messages) {
			message = formatGameMessage(message).replace("{seconds}", "" + seconds);
			Bukkit.broadcastMessage(translateAlternateColorCodes('&', message));
		}
	}
	
	@Override
	//Doesn't stop the game. Also prevents players from winning twice.
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (winners.contains(p)) return;
		if (e.getMessage().equalsIgnoreCase(wordToType)) {
			String command = plugin.getConfig().getString("gameOptions.rewardCommandSyntax");
			command = command.replace("{username}", p.getName()).replace("{reward}", reward.getReward()).replace("{amount}", "" + reward.getAmount());
			Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
			winners.add(p);
			sendWinnerMessage(p);
		}
	}
}
