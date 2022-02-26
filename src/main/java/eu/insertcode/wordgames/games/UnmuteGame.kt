package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.Permission
import eu.insertcode.wordgames.config.Config.GameOptions.Unmute.percentageOfCharactersToMute
import eu.insertcode.wordgames.config.Messages
import kotlin.math.floor

class UnmuteGame(instance: Main, wordToType: String, reward: Reward) : LongWordGame(instance, wordToType, reward) {
    init {
        showedWord = muteString(wordToType)
        sendGameMessage()
    }

    override val playPermission = Permission.PLAY_UNMUTE
    override val messageConfigPath = Messages.Games.unmute

    private fun muteString(string: String): String {
        val characters = string.toMutableList()
        val charactersToMute = floor(string.length.toDouble() / 100 * percentageOfCharactersToMute).toInt() + 1

        while (characters.count { it == '*' } < charactersToMute) {
            val randomChar = floor(Math.random() * string.length).toInt()
            characters[randomChar] = '*'
        }

        return characters.joinToString("")
    }
}