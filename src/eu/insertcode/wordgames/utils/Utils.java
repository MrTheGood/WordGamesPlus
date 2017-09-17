package eu.insertcode.wordgames.utils;

import static org.bukkit.ChatColor.translateAlternateColorCodes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Maarten de Goede - insertCode.eu Utility class
 */
public class Utils {
	
	public static String reorderString(String string) {
		// Reorder the input.
		List<Character> characters = new ArrayList<Character>();
		for (char c : string.toCharArray()) {
			characters.add(c);
		}
		StringBuilder reordered = new StringBuilder(string.length());
		while (characters.size() != 0) {
			int randPicker = (int) (Math.random() * characters.size());
			reordered.append(characters.remove(randPicker));
		}
		
		return "" + reordered;
	}
	
	public static String muteString(String string, Double percentage) {
		List<Character> characters = new ArrayList<Character>();
		for (char c : string.toCharArray()) {
			characters.add(c);
		}
		// Calculate how many characters should be muted.
		int charactersToMute = (int) Math.floor(((double) string.length() / 100) * percentage) + 1;
		
		// Create the string
		StringBuilder muted = new StringBuilder();
		for (char ch : characters) {
			muted.append(ch);
		}
		while (countCharacters(muted.toString(), '*') < charactersToMute) {
			// Calculate which letter to replace.
			int randomChar = (int) Math.floor(Math.random() * string.length());
			characters.set(randomChar, '*');
			
			// Create the new string
			muted.delete(0, muted.length());
			for (char ch : characters) {
				muted.append(ch);
			}
		}
		
		return "" + muted;
	}
	
	/**
	 * Counts how many times a certain character is in the text.
	 * 
	 * @param text
	 * @param character
	 *            the character to count
	 * @return how many times the character in the text is
	 */
	public static int countCharacters(String text, char character) {
		int amount = 0;
		int position = text.indexOf(character, 0);
		while (position >= 0) {
			amount++;
			position = text.indexOf(character, position + 1);
		}
		return amount;
	}
	
	/**
	 * Gets an error message from the configuration, replaces "{plugin}" with
	 * the specified string in the configuration and translates the chatcolors.
	 * 
	 * @param path
	 *            Path to the error message
	 * @return translated error message.
	 */
	@Deprecated
	public static String getErrorMessage(String path) {
		String returnMessage = ConfigManager.getMessages().getString(path).replace("{plugin}",
				ConfigManager.getMessages().getString("variables.plugin"));
		return translateAlternateColorCodes('&', returnMessage);
	}
	
	/**
	 * Gets a message from the config, puts it in an array and colours the message.
	 * 
	 * @param path
	 *            The path to the message.
	 * @return A coloured String array.
	 */
	public static String[] getColouredMessages(String path) {
		FileConfiguration msgconfig = ConfigManager.getMessages();
		String[] messages;
		messages = ConfigManager.getMessages().isList(path)
				? msgconfig.getStringList(path).toArray(new String[0]) : new String[] { msgconfig.getString(path) };
		
		for (int i = 0; i < messages.length; i++) {
			messages[i] = messages[i].replaceAll("{plugin}", msgconfig.getString("variables.plugin"));
			messages[i] = ChatColor.translateAlternateColorCodes('&', messages[i]);
		}
		
		return messages;
	}
}
