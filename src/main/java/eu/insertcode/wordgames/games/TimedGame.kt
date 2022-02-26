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

class TimedGame(instance: Main, wordToType: String, reward: Reward) : WordGame(instance, wordToType, reward) {
    private val seconds: Int = Config.GameOptions.Timed.secondsToType
    private val winners = ArrayList<Player>()

    init {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            for (message in getMessages(Messages.Games.Timed.stop)) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
                    formatGameMessage(message, wordToType)
                ))
            }
            endGame()
        }, (seconds * 20).toLong())
        sendGameMessage()
    }

    override val playPermission = Permission.PLAY_TIMED
    override val messageConfigPath = Messages.Games.Timed.start

    override fun formatGameMessage(message: String, word: String) =
        super.formatGameMessage(message, word).replace("{seconds}", "" + seconds)

    /**
     * Sends a message only to the winner, and doesn't broadcast it.
     */
    override fun sendWinnerMessage(winner: Player) {
        for (message in getMessages(Messages.Games.Timed.gameWon)) {
            winner.sendMessage(ChatColor.translateAlternateColorCodes('&',
                formatGameMessage(message, wordToType).replace("{player}", winner.displayName)
            ))
        }
    }

    /**
     * Doesn't stop the game when somebody wins.
     * Also prevents the same player from winning twice.
     */
    override fun onPlayerChat(e: AsyncPlayerChatEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            val player = e.player
            if (winners.contains(player)) return@Runnable

            val message = ChatColor.stripColor(e.message)!!.trim()
            if (message.equals(wordToType, ignoreCase = true)) {
                if (!Permission.PLAY_ALL.forPlayer(player, playPermission)) {
                    sendMessage(player, Messages.Error.noPlayPermissions, true)
                    return@Runnable
                }

                val command = Config.GameOptions.rewardCommandSyntax.replace("{username}", player.name).replace("{reward}", reward.reward).replace("{amount}", "" + reward.amount)

                Bukkit.dispatchCommand(plugin.server.consoleSender, command)
                winners.add(player)
                sendWinnerMessage(player)
            }
        })
    }
}