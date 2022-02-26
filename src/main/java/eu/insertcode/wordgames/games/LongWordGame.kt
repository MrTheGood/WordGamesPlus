package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.config.Config;
import eu.insertcode.wordgames.config.Messages;
import eu.insertcode.wordgames.message.MessageHandler;
import org.bukkit.Bukkit;

/**
 * The LongWordGame is any game that takes long enough for multiple game messages to be sent or for autoStop to kick in.
 * Pretty much every game except [TimedGame]
 */
abstract class LongWordGame extends WordGame {
	private final int repeatSendMessage;
	private int endWordGame;

	LongWordGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);

		long repeatTime = Config.GameOptions.Scheduler.INSTANCE.getTimerInSeconds();
		repeatSendMessage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::sendGameMessage, 20 * 10, repeatTime * 20);

		if (Config.GameOptions.AutoStop.INSTANCE.getEnabled()) {
			long endTime = Config.GameOptions.AutoStop.INSTANCE.getTimerInSeconds();

			endWordGame = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				for (String message : MessageHandler.INSTANCE.getColouredMessages(Messages.Games.autoStop))
					Bukkit.broadcastMessage(message.replace("{word}", this.wordToType));
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
