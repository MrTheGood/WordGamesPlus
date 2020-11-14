package eu.insertcode.wordgames;

import eu.insertcode.wordgames.games.*;
import eu.insertcode.wordgames.games.WordGame.Reward;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.*;

@SuppressWarnings("SameReturnValue")
public class CommandHandler implements CommandExecutor {
	private final Main plugin;
	
	CommandHandler(Main instance) {
		plugin = instance;
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		try {
			if (args[0].equalsIgnoreCase("help")) {
				return onCommandHelp(s);
			}
			if (args[0].equalsIgnoreCase("list")) {
				return onCommandList(s);
			}
			if (args[0].equalsIgnoreCase("stop")) {
				return onCommandStop(s);
			}
			if (args[0].equalsIgnoreCase("reload")) {
				return onCommandReload(s);
			}
			
			/*
			 * wordgames <type> <word> [amount] <reward>
			 */
			if (args.length >= 2) {
				//If the wordgames limit has been reached,
				int maxPlayingGames = plugin.getConfig().getInt("gameOptions.maxPlayingGames");
				if (plugin.wordGames.size() >= maxPlayingGames
						&& maxPlayingGames > 0) {
					return errorMessage(s, "error.tooManyGames");
				}
				
				//Test which wordgame the user is trying to create
				if (args[0].equalsIgnoreCase("calculate")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.calculate.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					// wordgames <type> [amount] <reward>
					return Permission.START_ALL.forSender(s, Permission.START_CALCULATE)
							? createCalculateGame(s, args)
							: errorMessage(s, "error.noPermissions");
				}
				if (args[0].equalsIgnoreCase("first")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.first.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					// wordgames <type> [amount] <reward>
					return Permission.START_ALL.forSender(s, Permission.START_FIRST)
							? createGame(s, args, Type.FIRST)
							: errorMessage(s, "error.noPermissions");
				}
				if (args[0].equalsIgnoreCase("hover")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.hover.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					return Permission.START_ALL.forSender(s, Permission.START_HOVER)
							? createGame(s, args, Type.HOVER)
							: errorMessage(s, "error.noPermissions");
				}
				if (args[0].equalsIgnoreCase("reorder")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.reorder.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					return Permission.START_ALL.forSender(s, Permission.START_REORDER)
							? createGame(s, args, Type.REORDER)
							: errorMessage(s, "error.noPermissions");
				}
				if (args[0].equalsIgnoreCase("unmute")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.unmute.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					return Permission.START_ALL.forSender(s, Permission.START_UNMUTE)
							? createGame(s, args, Type.UNMUTE)
							: errorMessage(s, "error.noPermissions");
				}
				if (args[0].equalsIgnoreCase("timed")) {
					boolean enabled = plugin.getConfig().getBoolean("gameOptions.timed.enabled", true);
					if (!enabled && !Permission.START_DISABLED.forSender(s)) {
						return errorMessage(s, "error.gameDisabled");
					}
					
					return Permission.START_ALL.forSender(s, Permission.START_TIMED)
							? createGame(s, args, Type.TIMED)
							: errorMessage(s, "error.noPermissions");
				}
				
				return errorMessage(s, "error.typeNotFound");
			}
		} catch (IndexOutOfBoundsException e) {
			return onCommandHelp(s);
		}
		return onCommandHelp(s);
	}
	
	@SuppressWarnings("SameReturnValue")
	private boolean onCommandHelp(CommandSender s) {
		s.sendMessage(GREEN + "/wordgames help" + DARK_GREEN + "  to show this message.");
		
		if (Permission.RELOAD.forSender(s))
			s.sendMessage(GREEN + "/wordgames reload" + DARK_GREEN + "  to reload the configuration.");
		
		if (Permission.LIST.forSender(s))
			s.sendMessage(GREEN + "/wordgames list" + DARK_GREEN + "  To see all currently playing games.");
		
		if (Permission.STOP.forSender(s))
			s.sendMessage(GREEN + "/wordgames stop" + DARK_GREEN + "  to stop any and all playing games.");
		
		
		if (Permission.START_ALL.forSender(s, Permission.START_REORDER))
			s.sendMessage(GREEN + "/wordgames reorder <word> [number] <reward>" + DARK_GREEN + "  to start the 'reorder' minigame.");
		
		if (Permission.START_ALL.forSender(s, Permission.START_FIRST))
			s.sendMessage(GREEN + "/wordgames first <word> [number] <reward>" + DARK_GREEN + "  to start the 'first' minigame.");
		
		if (Permission.START_ALL.forSender(s, Permission.START_HOVER))
			s.sendMessage(GREEN + "/wordgames hover <word> [number] <reward>" + DARK_GREEN + " to start the 'hover' minigame.");
		
		if (Permission.START_ALL.forSender(s, Permission.START_UNMUTE))
			s.sendMessage(GREEN + "/wordgames unmute <word> [number] <reward>" + DARK_GREEN + " to start the 'unmute' minigame.");
		
		if (Permission.START_ALL.forSender(s, Permission.START_TIMED))
			s.sendMessage(GREEN + "/wordgames timed <word> [number] <reward>" + DARK_GREEN + " to start the 'timed' minigame.");
		
		if (Permission.START_ALL.forSender(s, Permission.START_CALCULATE))
			s.sendMessage(GREEN + "/wordgames calculate [number] <reward>" + DARK_GREEN + " to start the 'calculate' minigame.");
		
		
		s.sendMessage(GOLD + "[" + DARK_RED + "WordGames+" + GOLD + "]" + DARK_GREEN + " Plugin version: " + plugin.getDescription().getVersion());
		return true;
	}
	
	private boolean onCommandList(CommandSender s) {
		//If the sender the required permissions
		if (!Permission.LIST.forSender(s)) {
			return errorMessage(s, "error.noPermissions");
		}
		
		s.sendMessage(Main.getColouredMessages("games.list.prefix"));
		for (int i = 0; i < plugin.wordGames.size(); i++) {
			s.sendMessage(DARK_GREEN.toString() + i + ".");
			for (String message : plugin.wordGames.get(i).getGameMessages()) {
				s.sendMessage(DARK_GREEN + "   " + message);
			}
		}
		s.sendMessage(Main.getColouredMessages("games.list.suffix"));
		return true;
	}
	
	private boolean onCommandStop(CommandSender s) {
		//If the sender the required permissions
		if (!Permission.STOP.forSender(s)) {
			return errorMessage(s, "error.noPermissions");
		}
		
		//If a game is playing.
		if (plugin.wordGames.size() != 0) {
			List<WordGame> wordGames = new ArrayList<>(plugin.wordGames);
			//Stop all broadcasts
			for (WordGame game : wordGames) {
				game.endGame();
			}
			
			//Stop all the games
			plugin.wordGames = new ArrayList<>();
			
			//Broadcast the stop
			for (String msg : Main.getColouredMessages("games.stop"))
				Bukkit.broadcastMessage(msg);
		} else {
			//No games are playing, tell sender!
			return errorMessage(s, "error.notPlaying");
		}
		return true;
	}
	
	private boolean onCommandReload(CommandSender s) {
		//If the sender the required permissions
		if (!Permission.RELOAD.forSender(s)) {
			return errorMessage(s, "error.noPermissions");
		}
		
		//Reload & send a message
		plugin.reload();
		s.sendMessage(Main.getColouredMessages("reload"));
		return true;
	}
	
	private boolean errorMessage(CommandSender s, String path) {
		s.sendMessage(Main.getColouredMessages(path));
		return true;
	}
	
	private boolean createGame(CommandSender s, String[] args, Type type) {
		String rewardString;
		int amount;
		String wordToType;
		//If the user filled an reward amount in.
		if (args.length < 4) {
			rewardString = args[2];
			amount = 1;
		} else {
			//Try to parse the amount
			//Never trust user input! :)
			try {
				amount = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				//Something went wrong, no need to panic! I got this.
				return errorMessage(s, "error.wrongInput");
			}
			rewardString = args[3];
		}
		
		Reward reward = new Reward(amount, rewardString);
		wordToType = args[1];
		switch (type) {
			case REORDER:
				plugin.wordGames.add(new ReorderGame(plugin, wordToType, reward));
				return true;
			case FIRST:
				plugin.wordGames.add(new FirstGame(plugin, wordToType, reward));
				return true;
			case HOVER:
				plugin.wordGames.add(new HoverGame(plugin, wordToType, reward));
				return true;
			case UNMUTE:
				plugin.wordGames.add(new UnmuteGame(plugin, wordToType, reward));
				return true;
			case TIMED:
				plugin.wordGames.add(new TimedGame(plugin, wordToType, reward));
				return true;
			default:
				return false;
		}
	}
	
	private boolean createCalculateGame(CommandSender s, String[] args) {
		String rewardString;
		int amount;
		//If the user filled an reward amount in.
		if (args.length < 3) {
			rewardString = args[1];
			amount = 1;
		} else {
			try {
				amount = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				return errorMessage(s, "error.wrongInput");
			}
			rewardString = args[2];
		}
		
		Reward reward = new Reward(amount, rewardString);
		plugin.wordGames.add(new CalculateGame(plugin, "", reward));
		return true;
	}
	
	private enum Type {HOVER, FIRST, REORDER, UNMUTE, TIMED}
}
