package eu.insertcode.wordgames;

import org.bukkit.Bukkit;

import java.util.List;

import eu.insertcode.wordgames.games.CalculateGame;
import eu.insertcode.wordgames.games.HoverGame;
import eu.insertcode.wordgames.games.ReorderGame;
import eu.insertcode.wordgames.games.TimedGame;
import eu.insertcode.wordgames.games.UnmuteGame;
import eu.insertcode.wordgames.games.WordGame;
import eu.insertcode.wordgames.games.WordGame.Reward;

class AutoStart {
	private static Main plugin;
	
	public static void setPlugin(Main instance) {
		plugin = instance;
	}
	
	
	static void autoStart() {
		if (!plugin.getConfig().getBoolean("autoStart.enabled")) {
			// Warn the console that it is disabled.
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] autoStart is NOT enabled. Please edit the configuration if you want to enable it.");
			return;
		}
		
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
			
			plugin.wordGames.add(getRandomGameType(wordToType, reward));
		}, 20 * 10, plugin.getConfig().getLong("gameOptions.scheduler.timerInSeconds") * 20);
	}
	
	
	private static WordGame getRandomGameType(String wordToType, Reward reward) {
		switch ((int) Math.ceil(Math.random() * 5)) {
			case 1:
				return new HoverGame(plugin, wordToType, reward);
			case 2:
				return new ReorderGame(plugin, wordToType, reward);
			case 3:
				return new TimedGame(plugin, wordToType, reward);
			case 4:
				return new CalculateGame(plugin, "", reward);
			default:
				return new UnmuteGame(plugin, wordToType, reward);
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
