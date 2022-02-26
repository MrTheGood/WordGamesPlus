package eu.insertcode.wordgames.games;

import eu.insertcode.wordgames.Main;
import eu.insertcode.wordgames.Permission;
import eu.insertcode.wordgames.config.ConfigManager;
import eu.insertcode.wordgames.config.Messages;
import eu.insertcode.wordgames.message.MessageHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

public class HoverGame extends LongWordGame {
	private final static String DELIMITER = "((?<=\\Q%1$s\\E)|(?=\\Q%1$s\\E))";
	private final List<BaseComponent[]> showedMessages = new ArrayList<>();

	public HoverGame(Main instance, String wordToType, Reward reward) {
		super(instance, wordToType, reward);

		List<String> messages = MessageHandler.INSTANCE.getMessages(Messages.Games.hover);
		for (String message : messages) {
			message = message
					.replace("{amount}", "" + reward.getAmount())
					.replace("{reward}", reward.getReward());

			//Split the string just before and just after {word}
			String[] inProgress = message.split(String.format(DELIMITER, "{word}"));

			BaseComponent[] components = new BaseComponent[inProgress.length];
			for (int i = 0; i < inProgress.length; i++) {
				if (inProgress[i].equalsIgnoreCase("{word}")) {
					TextComponent hoverComponent = new TextComponent(translateAlternateColorCodes('&', ConfigManager.INSTANCE.getMessagesConfig().getString(Messages.Variables.hover)));
					hoverComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(wordToType)));
					components[i] = hoverComponent;
				} else {
					components[i] = new TextComponent(translateAlternateColorCodes('&', inProgress[i]));
				}
			}
			showedMessages.add(components);
		}
		sendGameMessage();
	}
	
	@Override
	public Permission getPlayPermission() {
		return Permission.PLAY_HOVER;
	}
	
	@Override
	String getMessageConfigPath() {
		return null;
	}
	
	/**
	 * This version is specifically only for the console
	 */
	@Override
	public String[] getGameMessages() {
		List<String> messages = MessageHandler.INSTANCE.getMessages(Messages.Games.hover);
		for (int i = 0; i < messages.size(); i++) {
			messages.set(i, messages.get(i)
				.replace("{amount}", "" + reward.getAmount())
				.replace("{reward}", reward.getReward())
				.replace("{word}", ConfigManager.INSTANCE.getMessagesConfig().getString(Messages.Variables.hover) + "[" + wordToType + "]"));
			messages.set(i, translateAlternateColorCodes('&', messages.get(i)));
		}

		String[] result = new String[messages.size()];
		result = messages.toArray(result);
		return result;
	}
	
	@Override
	void sendGameMessage() {
		for (BaseComponent[] message : showedMessages) {
			for (Player p : plugin.getServer().getOnlinePlayers())
				p.spigot().sendMessage(message);
		}

		// Send the message to the console as well
		Bukkit.getConsoleSender().sendMessage(getGameMessages());
	}
	
}
