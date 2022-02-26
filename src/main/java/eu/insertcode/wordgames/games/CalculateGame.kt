package eu.insertcode.wordgames.games

import eu.insertcode.wordgames.Main
import eu.insertcode.wordgames.Permission
import eu.insertcode.wordgames.config.Messages
import kotlin.math.roundToInt

class CalculateGame(instance: Main, wordToType: String, reward: Reward) : LongWordGame(instance, wordToType, reward) {

    override val playPermission = Permission.PLAY_CALCULATE
    override val messageConfigPath = Messages.Games.calculate

    init {
        val numberOne = (Math.random() * 51).toInt()
        val numberTwo = (Math.random() * 20).toInt() + 1

        when ((Math.random() * 4).toInt()) {
            1 -> {
                this.wordToType = (numberOne + numberTwo).toString()
                showedWord = "$numberOne + $numberTwo"
            }
            2 -> {
                this.wordToType = (numberOne - numberTwo).toString()
                showedWord = "$numberOne - $numberTwo"
            }
            3 -> {
                this.wordToType = (numberOne * numberTwo).toString()
                showedWord = "$numberOne * $numberTwo"
            }
            else -> {
                this.wordToType = (numberOne.toDouble() / numberTwo).roundToInt().toString()
                showedWord = "$numberOne / $numberTwo"
            }
        }

        sendGameMessage()
    }

}