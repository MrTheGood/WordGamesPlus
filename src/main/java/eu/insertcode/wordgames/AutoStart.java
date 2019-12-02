package eu.insertcode.wordgames;

import eu.insertcode.wordgames.games.*;
import eu.insertcode.wordgames.games.WordGame.Reward;
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
		if (!plugin.getConfig().getBoolean("autoStart.enabled")) {
			// Warn the console that it is disabled.
			plugin.getLogger().warning("autoStart is NOT enabled. Please edit the configuration if you want to enable it.");
			return;
		}
		
		long seconds = plugin.getConfig().getLong("gameOptions.scheduler.timerInSeconds");
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			// If there already are games playing or there are not enough players online,
			if (plugin.wordGames.size() > 0 || Bukkit.getOnlinePlayers().size() < plugin.getConfig().getInt("autoStart.minimumPlayers"))
				return;
			
			String wordToType = getRandomWordToType();
			Reward reward = getRandomReward();
			
			// Test if this shit isn't null..
			if (reward == null || wordToType == null) {
				Bukkit.getConsoleSender().sendMessage(Main.getColouredMessages("error.configWrong"));
				return;
			}
			
			WordGame randomGame = getRandomGameType(wordToType, reward);
			if (randomGame == null) {
				Bukkit.getConsoleSender().sendMessage(Main.getColouredMessages("error.noGamesEnabled"));
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
		if (plugin.getConfig().getBoolean("gameOptions.calculate.enabled", true))
			gameTypeOptions.add("calculate");
		
		if (plugin.getConfig().getBoolean("gameOptions.hover.enabled", true))
			gameTypeOptions.add("hover");
		
		if (plugin.getConfig().getBoolean("gameOptions.reorder.enabled", true))
			gameTypeOptions.add("reorder");
		
		if (plugin.getConfig().getBoolean("gameOptions.unmute.enabled", true))
			gameTypeOptions.add("unmute");
		
		if (plugin.getConfig().getBoolean("gameOptions.timed.enabled", true))
			gameTypeOptions.add("timed");
		
		
		if (gameTypeOptions.isEmpty())
			return null;
		
		int i = (int) Math.floor(Math.random() * gameTypeOptions.size());
		switch (gameTypeOptions.get(i)) {
			case "calculate":
				return new CalculateGame(plugin, "", reward);
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
		List<String> words = plugin.getConfig().getStringList("autoStart.words");
		return words.isEmpty() ? null : words.get((int) Math.floor(Math.random() * words.size()));
	}
	
	
	private static Reward getRandomReward() {
		List<String> rewards = plugin.getConfig().getStringList("autoStart.rewards");
		
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
