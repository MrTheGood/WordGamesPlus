package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.Permission
import eu.insertcode.wordgames.config.ConfigManager.messagesConfig
import eu.insertcode.wordgames.config.Messages
import eu.insertcode.wordgames.message.MessageHandler.getMessages
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.ChatColor

class HoverGame(instance: Main?, wordToType: String?, reward: Reward) : LongWordGame(instance!!, wordToType!!, reward) {
    private val showedMessages: MutableList<Array<BaseComponent?>> = ArrayList()

    init {
        for (message in getMessages(Messages.Games.hover)) {
            val message = message
                .replace("{amount}", "" + reward.amount)
                .replace("{reward}", reward.reward)

            // Split the string just before and just after {word}
            val components = message.split(String.format(DELIMITER, "{word}")).map {
                if (it.equals("{word}", ignoreCase = true))
                    TextComponent(ChatColor.translateAlternateColorCodes('&', messagesConfig.getString(Messages.Variables.hover)!!)).apply {
                        hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text(wordToType))
                    }
                else TextComponent(ChatColor.translateAlternateColorCodes('&', it))
            }

            showedMessages.add(components.toTypedArray())
        }
        sendGameMessage()
    }

    override val playPermission = Permission.PLAY_HOVER
    override val messageConfigPath = ""

    /**
     * This version is specifically only for the console
     */
    override val gameMessages =
        getMessages(Messages.Games.hover).map {
            ChatColor.translateAlternateColorCodes('&',
                it.replace("{amount}", "" + reward.amount)
                    .replace("{reward}", reward.reward)
                    .replace("{word}", messagesConfig.getString(Messages.Variables.hover) + "[" + wordToType + "]")
            )
        }

    override fun sendGameMessage() {
        for (message in showedMessages) {
            for (player in plugin.server.onlinePlayers) player.spigot().sendMessage(*message)
        }

        Bukkit.getConsoleSender().sendMessage(gameMessages.toTypedArray())
    }

    companion object {
        private const val DELIMITER = "((?<=\\Q%1\$s\\E)|(?=\\Q%1\$s\\E))"
    }
}