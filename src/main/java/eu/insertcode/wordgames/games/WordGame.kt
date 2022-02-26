package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.Permission
import eu.insertcode.wordgames.config.Config
import eu.insertcode.wordgames.config.Messages
import eu.insertcode.wordgames.message.MessageHandler.getMessages
import eu.insertcode.wordgames.message.MessageHandler.sendMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.AsyncPlayerChatEvent

abstract class WordGame(
    val plugin: Main,
    var wordToType: String,
    val reward: Reward,
    var showedWord: String = wordToType
) {

    open fun endGame() {
        plugin.removeGame(this)
    }

    open fun sendWinnerMessage(winner: Player) {
        for (message in getMessages(Messages.Games.gameWon)) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                formatGameMessage(message, wordToType).replace("{player}", winner.displayName)
            ))
        }
    }

    abstract val playPermission: Permission
    abstract val messageConfigPath: String
    open val gameMessages: List<String>
        get() = getMessages(messageConfigPath).map {
            ChatColor.translateAlternateColorCodes('&', formatGameMessage(it, showedWord))
        }

    open fun sendGameMessage() {
        for (message in gameMessages) {
            Bukkit.broadcastMessage(message)
        }
    }

    open fun formatGameMessage(message: String, word: String) =
        message.replace("{word}", word)
            .replace("{amount}", reward.amount.toString())
            .replace("{reward}", reward.reward)

    open fun onPlayerChat(e: AsyncPlayerChatEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val p = e.player
            //If the player types the correct word.
            val message = ChatColor.stripColor(e.message)!!.trim { it <= ' ' }
            if (message.equals(wordToType, ignoreCase = true)) {
                if (!Permission.PLAY_ALL.forPlayer(p, playPermission)) {
                    sendMessage(p, Messages.Error.noPlayPermissions, true)
                    return@Runnable
                }

                val command = Config.GameOptions.rewardCommandSyntax.replace("{username}", p.name).replace("{reward}", reward.reward).replace("{amount}", "" + reward.amount)
                Bukkit.dispatchCommand(plugin.server.consoleSender, command)
                sendWinnerMessage(p)
                endGame()
            }
        })
    }
}