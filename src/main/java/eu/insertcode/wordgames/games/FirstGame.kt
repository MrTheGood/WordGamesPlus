package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.Permission
import eu.insertcode.wordgames.config.Messages

class FirstGame(instance: Main, wordToType: String, reward: Reward) : LongWordGame(instance, wordToType, reward) {
    init {
        showedWord = wordToType
        sendGameMessage()
    }

    override val playPermission = Permission.PLAY_FIRST
    override val messageConfigPath = Messages.Games.first
}