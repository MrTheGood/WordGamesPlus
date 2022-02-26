package eu.insertcode.wordgames;

import eu.insertcode.wordgames.config.Config;
import eu.insertcode.wordgames.config.Messages;
import eu.insertcode.wordgames.games.*;
import eu.insertcode.wordgames.games.WordGame.Reward;
import eu.insertcode.wordgames.message.MessageHandler;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class AutoStart {
	private static Main plugin;
	
	public static void setPlugin(Main instance) {
		plugin = instance;
	}
	
	
	static void autoStart() {
		if (!Config.AutoStart.INSTANCE.getEnabled()) {
			// Warn the console that it is disabled.
			plugin.getLogger().warning("autoStart is NOT enabled. Please edit the configuration if you want to enable it.");
			return;
		}

		long seconds = Config.GameOptions.Scheduler.INSTANCE.getTimerInSeconds();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			// If there already are games playing or there are not enough players online,
			if (plugin.wordGames.size() > 0 || Bukkit.getOnlinePlayers().size() < Config.AutoStart.INSTANCE.getMinimumPlayers())
				return;
			
			String wordToType = getRandomWordToType();
			Reward reward = getRandomReward();
			
			// Test if this shit isn't null..
			if (reward == null || wordToType == null) {
				MessageHandler.INSTANCE.sendMessage(Bukkit.getConsoleSender(), Messages.Error.configWrong, true);
				return;
			}
			
			WordGame randomGame = getRandomGameType(wordToType, reward);
			if (randomGame == null) {
				MessageHandler.INSTANCE.sendMessage(Bukkit.getConsoleSender(), Messages.Error.noGamesEnabled, true);
				return;
			}
			plugin.wordGames.add(randomGame);
		}, 20 * 10, seconds * 20);
	}
	
	
	/**
	 * Note to self: This code works. Don't refactor everything. You don't have the time.
	 *
	 * @return a random [WordGame], or null if all types are disabled
	 */
	@Nullable
	private static WordGame getRandomGameType(String wordToType, Reward reward) {
		ArrayList<String> gameTypeOptions = new ArrayList<>();
		if (Config.GameOptions.Calculate.INSTANCE.getEnabled())
			gameTypeOptions.add("calculate");

		if (Config.GameOptions.First.INSTANCE.getEnabled())
			gameTypeOptions.add("first");

		if (Config.GameOptions.Hover.INSTANCE.getEnabled())
			gameTypeOptions.add("hover");

		if (Config.GameOptions.Reorder.INSTANCE.getEnabled())
			gameTypeOptions.add("reorder");

		if (Config.GameOptions.Unmute.INSTANCE.getEnabled())
			gameTypeOptions.add("unmute");

		if (Config.GameOptions.Timed.INSTANCE.getEnabled())
			gameTypeOptions.add("timed");
		
		
		if (gameTypeOptions.isEmpty())
			return null;
		
		int i = (int) Math.floor(Math.random() * gameTypeOptions.size());
		switch (gameTypeOptions.get(i)) {
			case "calculate":
				return new CalculateGame(plugin, "", reward);
			case "first":
				return new FirstGame(plugin, wordToType, reward);
			case "hover":
				return new HoverGame(plugin, wordToType, reward);
			case "reorder":
				return new ReorderGame(plugin, wordToType, reward);
			case "unmute":
				return new UnmuteGame(plugin, wordToType, reward);
			default:
				return new TimedGame(plugin, wordToType, reward);
		}
	}
	
	private static String getRandomWordToType() {
		List<String> words = Config.AutoStart.INSTANCE.getWords();
		return words.isEmpty() ? null : words.get((int) Math.floor(Math.random() * words.size()));
	}
	
	
	private static Reward getRandomReward() {
		List<String> rewards = Config.AutoStart.INSTANCE.getRewards();
		
		if (rewards.isEmpty()) return null;//Config wrong
		
		String[] rawReward = rewards.get((int) Math.floor(Math.random() * rewards.size())).split(" ");
		int amount;
		String reward;
		
		// Test if the configuration is correct.
		if (rawReward.length <= 0) {
			return null;//Config wrong
		}
		
		try {
			reward = rawReward[1];
			amount = Integer.parseInt(rawReward[0]);
		} catch (NumberFormatException e) {
			return null;//Config wrong
		} catch (IndexOutOfBoundsException e) {
			// The user didn't fill a reward amount in.
			reward = rawReward[0];
			amount = 1;
		}
		
		return new Reward(amount, reward);
	}
	
}
