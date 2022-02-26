package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.config.Config
import eu.insertcode.wordgames.config.Messages
import eu.insertcode.wordgames.message.MessageHandler.getColouredMessages
import org.bukkit.Bukkit

/**
 * The LongWordGame is any game that takes long enough for multiple game messages to be sent or for autoStop to kick in.
 * Pretty much every game except [TimedGame]
 */
abstract class LongWordGame(instance: Main, wordToType: String, reward: Reward) : WordGame(instance, wordToType, reward) {
    private val repeatSendMessage: Int
    private var endWordGame = 0

    init {
        val repeatTime = Config.GameOptions.Scheduler.timerInSeconds
        repeatSendMessage = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, ::sendGameMessage, (20 * 10).toLong(), repeatTime * 20)

        if (Config.GameOptions.AutoStop.enabled) {
            val endTime = Config.GameOptions.AutoStop.timerInSeconds
            endWordGame = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                for (message in getColouredMessages(Messages.Games.autoStop)) Bukkit.broadcastMessage(message.replace("{word}", this.wordToType))
                endGame()
            }, endTime * 20)
        }
    }

    override fun endGame() {
        super.endGame()
        Bukkit.getScheduler().cancelTask(repeatSendMessage)
        Bukkit.getScheduler().cancelTask(endWordGame)
    }
}