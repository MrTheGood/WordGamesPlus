package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;

import eu.insertcode.wordgames.Main;

/**
 * The LongWordGame is any game that takes long enough for multiple game messages to be sent or for autoStop to kick in.
 * Pretty much every game except [TimedGame]
 */
abstract class LongWordGame extends WordGame {
	private int repeatSendMessage, endWordGame;
	
	LongWordGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		
		int repeatTime = plugin.getConfig().getInt("gameOptions.scheduler.timerInSeconds");
		repeatSendMessage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::sendGameMessage, 20 * 10, repeatTime * 20);
		
		if (plugin.getConfig().getBoolean("gameOptions.autoStop.enabled")) {
			int endTime = plugin.getConfig().getInt("gameOptions.autoStop.timerInSeconds");
			
			endWordGame = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				for (String message : Main.getColouredMessages("games.autoStop"))
					Bukkit.broadcastMessage(message.replace("{word}", wordToType));
				endGame();
			}, endTime * 20);
		}
	}
	
	@Override
	public void endGame() {
		super.endGame();
		Bukkit.getScheduler().cancelTask(repeatSendMessage);
		Bukkit.getScheduler().cancelTask(endWordGame);
	}
}
