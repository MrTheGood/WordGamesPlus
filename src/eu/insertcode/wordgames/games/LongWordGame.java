package eu.insertcode.wordgames.games;

import org.bukkit.Bukkit;

import eu.insertcode.wordgames.Main;

/**
 * The LongWordGame is any game that takes long enough for multiple game messages to be sent.
 * Pretty much every game except [TimedGame]
 */
abstract class LongWordGame extends WordGame {
	private int schedulerID;
	
	LongWordGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);
		int time = plugin.getConfig().getInt("gameOptions.scheduler.timerInSeconds");
		schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::sendGameMessage, 20 * 10, time * 20);
	}
	
	@Override
	public void endGame() {
		super.endGame();
		Bukkit.getScheduler().cancelTask(schedulerID);
	}
}
