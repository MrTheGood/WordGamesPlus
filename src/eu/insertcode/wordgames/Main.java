package eu.insertcode.wordgames;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.DARK_RED;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.GREEN;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import eu.insertcode.wordgames.compatibility.Compatibility;
import eu.insertcode.wordgames.compatibility.Compatibility_1_10_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_11_R1;
import eu.insertcode.wordgames.compatibility.Compatibility_1_12_R1;

/**
 * @author Maarten de Goede - insertCode.eu
 * Main class
 */
@SuppressWarnings("deprecation")
public class Main extends JavaPlugin implements Listener {
	public ConfigurationManager configManager;
	
	private Compatibility compatibility;
	private PluginDescriptionFile pdfFile = getDescription();
	private ArrayList<WordGame> wordGames = new ArrayList<WordGame>();
	//TODO: Ruim deze code op.. -_-
	//TODO: Als er een update is, warn het in de console. (Via website database)
	
	Utils utils;
	
	@Override
	public void onEnable() {
		if (setup()) {
			//Register the plugin events in this class
			getServer().getPluginManager().registerEvents(this, this);

			configManager = new ConfigurationManager();
			configManager.createFiles(this);
			utils = new Utils(this);
			autoStart();			//Start the autoStart scheduler.
		} else {
			getLogger().severe("Failed to setup WordGames+!");
			getLogger().severe("Your server version is not compatible with this plugin!");
			
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}
	
	private boolean setup() {
		String version;
		
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}
		
		getLogger().info("[WordGames+, insertCode] Your server is running " + version);

		if (version.equals("v1_12_R1")) {
			//Server is running 1.12 so we need to use the 1.12 R1 NMS class
			compatibility = new Compatibility_1_12_R1();
			return true;
		} 
		
		if (version.equals("v1_11_R1")) {
			//Server is running 1.11 so we need to use the 1.11 R1 NMS class
			compatibility = new Compatibility_1_11_R1();
			return true;
		} 

		if (version.equals("v1_10_R1")) {
			//Server is running 1.10 so we need to use the 1.10 R1 NMS class
			compatibility = new Compatibility_1_10_R1();
			return true;
		}
		
		return false;
	}
	
	public Compatibility getCompatibility() {
		return compatibility;
	}
	
	
	public void autoStart() {
		//If autoStart is enabled,
		if (getConfig().getBoolean("autoStart.enabled")) {
			final Main plugin = this;
			//Start the scheduler.
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				public void run() {
					//If there are no games playing,
					if (wordGames.size() == 0) {
						//If the minimum number of players is online,
						if (Bukkit.getOnlinePlayers().size() >= getConfig().getInt("autoStart.minimumPlayers")) {
							List<String> words = getConfig().getStringList("autoStart.words");
							List<String> rewards = getConfig().getStringList("autoStart.rewards");
							//If rewards & words are NOT empty,
							if (words.size() > 0 && rewards.size() > 0) {
								WordGame wg = new WordGame(plugin);
								
								//Use a random word from the list.
								wg.wordToType = words.get((int)Math.floor(Math.random() * words.size()));
								//Use a random reward from the list and split it at a space.
								String[] rawReward = rewards.get((int)Math.floor(Math.random() * rewards.size())).split(" ");
								
								//Test if the configuration is correct.
								if (rawReward.length > 0) {
									//If the user filled a reward amount in.
									if (rawReward.length > 1) {
										//Try to parse the amount
										//Never trust user input! :)
										try {
											wg.amount = Integer.parseInt(rawReward[0]);
										} catch (NumberFormatException e) {
											//Something went wrong, no need to panic! I got this.
											Bukkit.getConsoleSender().sendMessage(utils.getErrorMessage("error.configWrong"));
											return;
										}
										wg.reward = rawReward[1];
									} else {
										//The user didn't fill a reward amount in.
										wg.reward = rawReward[0];
										wg.amount = 1;
									}
									
									int randomPicker = (int)Math.ceil(Math.random() * 3);
									if (randomPicker == 1) {
										wg.wordGameType = WordGame.Type.HOVER;
									} 
									else if (randomPicker == 2) {
										wg.showedWord = utils.reorderString(wg.wordToType);
										wg.wordGameType = WordGame.Type.REORDER;
									} 
									else {
										wg.showedWord = utils.muteString(wg.wordToType, getConfig().getDouble("gameOptions.unmute.percentageOfCharactersToMute"));
										wg.wordGameType = WordGame.Type.UNMUTE;
									}
									
									wg.sendGameMessage();
									wg.startAutoBroadcaster();
									wordGames.add(wg);
									return;
								} else {
									Bukkit.getConsoleSender().sendMessage(utils.getErrorMessage("error.configWrong"));
								}
							} else {
								Bukkit.getConsoleSender().sendMessage(utils.getErrorMessage("error.configWrong"));
							}
						}
					}
				}
			}, 20 * 10, getConfig().getLong("gameOptions.scheduler.timerInSeconds") * 20);
		} else {
			//Warn the console that it is disabled.
			Bukkit.getConsoleSender().sendMessage("[<WordGames+, insertCode>] autoStart is NOT enabled. Please edit the configuration if you want to enable it.");
		}
	}
	
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent e) {
		//For all games
		for (int i = 0; i < wordGames.size(); i++) {
			WordGame wg = wordGames.get(i);
			Player p = e.getPlayer();
			
			//If the player types the correct word.
			if (e.getMessage().equalsIgnoreCase(wg.wordToType)) {
				String command = getConfig().getString("gameOptions.rewardCommandSyntax");
				command = command.replace("{username}", p.getName()).replace("{reward}", wg.reward).replace("{amount}", "" + wg.amount);
				Bukkit.dispatchCommand(getServer().getConsoleSender(), command);
				wg.sendWinnerMessage(p);
				wg.stopAutoBroadcaster();
				
				//reset everything.
				wordGames.remove(i);
			}
		}
	}
	
	
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		//If the user uses the main command
		if (cmd.getName().equalsIgnoreCase("wordgame")) {
			//If the sender fills in the least amount of arguments.
			if (args.length >= 1) {
				/**
				 * wordgames help
				 */
				if (args[0].equalsIgnoreCase("help")) {
					s.sendMessage(GREEN + "/wordgames help" + DARK_GREEN + "  to show this message.");
					if (s.hasPermission("wordgamesplus.reload"))
						s.sendMessage(GREEN + "/wordgames reload" + DARK_GREEN + "  to reload the configuration.");
					if (s.hasPermission("wordgamesplus.stop"))
						s.sendMessage(GREEN + "/wordgames stop" + DARK_GREEN + "  to stop any and all playing games.");
					if (s.hasPermission("wordgamesplus.start")) {
						s.sendMessage(GREEN + "/wordgames reorder <word> [number] <reward>" + DARK_GREEN + "  to start the 'reorder' minigame.");
						s.sendMessage(GREEN + "/wordgames hover <word> [number] <reward>" + DARK_GREEN + " to start the 'hover' minigame.");
						s.sendMessage(GREEN + "/wordgames unmute <word> [number] <reward>" + DARK_GREEN + " to start the 'unmute' minigame.");
					}
					s.sendMessage(GOLD + "[" + DARK_RED + "WordGames+" + GOLD + "]" + DARK_GREEN + " Plugin version: " + pdfFile.getVersion());
					return true;
				}
				
				/**
				 * wordgames stop
				 */
				if (args[0].equalsIgnoreCase("stop")) {
					//If the sender the required permissions
					if (!s.hasPermission("wordgamesplus.stop")) {
						s.sendMessage(utils.getErrorMessage("error.noPermissions"));
						return true;
					}
					
					//If a game is playing.
					if (wordGames.size() != 0) {
						//Broadcast the stop
						Bukkit.broadcastMessage(utils.getErrorMessage("games.stop"));
						//Stop all broadcasts
						for (WordGame game : wordGames) {
							game.stopAutoBroadcaster();
						}
						
						//Stop all the games
						wordGames = new ArrayList<WordGame>();
					} else {
						//No games are playing, tell sender!
						s.sendMessage(utils.getErrorMessage("error.notPlaying"));
					}
					return true;
				}
				
				/**
				 * wordgames reload
				 */
				if (args[0].equalsIgnoreCase("reload")) {
					//If the sender the required permissions
					if (!s.hasPermission("wordgamesplus.reload")) {
						s.sendMessage(utils.getErrorMessage("error.noPermissions"));
						return true;
					}
					
					//Reload & send a message
					reloadConfig();
//					configManager.reloadData();TODO
					configManager.reloadMessages();
					//This isn't an error message. I just didn't feel like adding a whole new method.
					s.sendMessage(utils.getErrorMessage("reload"));
					return true;
				}
				
				/**
				 * wordgames (type) [amount] (reward)
				 */
				if (args.length >= 3) {
					//If the wordgames limit has been reached,
					if (wordGames.size() >= getConfig().getInt("gameOptions.maxPlayingGames") && getConfig().getInt("gameOptions.maxPlayingGames") != 0) {
						//Send a warning.
						s.sendMessage(utils.getErrorMessage("error.tooManyGames"));
						return true;
					}
					//If the sender the required permissions
					if (!s.hasPermission("wordgamesplus.start")) {
						s.sendMessage(utils.getErrorMessage("error.noPermissions"));
						return true;
					}
					
					
					/**
					 * wordgames hover
					 */
					if (args[0].equalsIgnoreCase("hover")) {
						WordGame wg = new WordGame(this);
						//If the user filled an reward amount in.
						if (args.length < 4) {
							wg.reward = args[2];
							wg.amount = 1;
						} else {
							//Try to parse the amount
							//Never trust user input! :)
							try {
								wg.amount = Integer.parseInt(args[2]);
							} catch (Exception e) {
								//Something went wrong, no need to panic! I got this.
								s.sendMessage(utils.getErrorMessage("error.wrongInput"));
								return true;
							}
							wg.reward = args[3];
						}

						wg.wordToType = args[1];
						wg.wordGameType = WordGame.Type.HOVER;
						wg.sendGameMessage();
						wg.startAutoBroadcaster();
						wordGames.add(wg);
						return true;
					}
					
					
					/**
					 * wordgames reorder
					 */
					if (args[0].equalsIgnoreCase("reorder")) {
						WordGame wg = new WordGame(this);
						//If the user filled an reward amount in.
						if (args.length < 4) {
							wg.reward = args[2];
							wg.amount = 1;
						} else {
							//Try to parse the amount
							//Never trust user input! :)
							try {
								wg.amount = Integer.parseInt(args[2]);
							} catch (Exception e) {
								//Something went wrong, no need to panic! I got this.
								s.sendMessage(utils.getErrorMessage("error.wrongInput"));
								return true;
							}
							wg.reward = args[3];
						}

						wg.wordToType = args[1];
						wg.wordGameType = WordGame.Type.REORDER;
						wg.showedWord = utils.reorderString(wg.wordToType);
						wg.sendGameMessage();
						wg.startAutoBroadcaster();
						wordGames.add(wg);
						return true;
					}					
					
					
					/**
					 * wordgames unmute
					 */
					if (args[0].equalsIgnoreCase("unmute")) {
						WordGame wg = new WordGame(this);
						//If the user filled an reward amount in.
						if (args.length < 4) {
							wg.reward = args[2];
							wg.amount = 1;
						} else {
							//Try to parse the amount
							//Never trust user input! :)
							try {
								wg.amount = Integer.parseInt(args[2]);
							} catch (Exception e) {
								//Something went wrong, no need to panic! I got this.
								s.sendMessage(utils.getErrorMessage("error.wrongInput"));
								return true;
							}
							wg.reward = args[3];
						}

						wg.wordToType = args[1];
						wg.wordGameType = WordGame.Type.UNMUTE;
						wg.showedWord = utils.muteString(wg.wordToType, getConfig().getDouble("gameOptions.unmute.percentageOfCharactersToMute"));
						wg.sendGameMessage();
						wg.startAutoBroadcaster();
						wordGames.add(wg);
						return true;
					}
					
					s.sendMessage(utils.getErrorMessage("error.typeNotFound"));
					return true;
				}
				s.sendMessage(utils.getErrorMessage("error.notEnoughArguments"));
				return true;
			}
			s.sendMessage(utils.getErrorMessage("error.notEnoughArguments"));
			return true;
		}
		return false;
	}
}











