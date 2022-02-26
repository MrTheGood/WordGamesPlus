package eu.insertcode.wordgames.config

import org.bukkit.configuration.file.FileConfiguration

/**
 * Created by maartendegoede on 18/05/2020.
 * Copyright © 2020 insertCode.eu. All rights reserved.
 */
object Config {
    private lateinit var configuration: FileConfiguration

    fun initialize(config: FileConfiguration) {
        configuration = config
    }

    object GameOptions {
        private const val gameOptions = "gameOptions"

        object AutoStop {
            private const val autoStop = "autoStop"

            val enabled get() = configuration.getBoolean("$gameOptions.$autoStop.enabled")
            val timerInSeconds get() = configuration.getLong("$gameOptions.$autoStop.timerInSeconds")
        }

        val rewardCommandSyntax get() = configuration.getString("$gameOptions.rewardCommandSyntax")!!
        val maxPlayingGames get() = configuration.getInt("$gameOptions.maxPlayingGames")

        object Scheduler {
            val timerInSeconds get() = configuration.getLong("$gameOptions.scheduler.timerInSeconds")
        }

        object Calculate {
            val enabled get() = configuration.getBoolean("$gameOptions.calculate.enabled", true)
        }

        object First {
            val enabled get() = configuration.getBoolean("$gameOptions.first.enabled", true)
        }

        object Hover {
            val enabled get() = configuration.getBoolean("$gameOptions.hover.enabled", true)
        }

        object Reorder {
            val enabled get() = configuration.getBoolean("$gameOptions.reorder.enabled", true)
        }

        object Unmute {
            private const val unmute = "unmute"

            val enabled get() = configuration.getBoolean("$gameOptions.$unmute.enabled", true)
            val percentageOfCharactersToMute get() = configuration.getInt("$gameOptions.$unmute.percentageOfCharactersToMute")
        }

        object Timed {
            private const val timed = "timed"

            val enabled get() = configuration.getBoolean("$gameOptions.$timed.enabled", true)
            val secondsToType get() = configuration.getInt("$gameOptions.$timed.secondsToType")
        }
    }

    object AutoStart {
        private const val autoStart = "autoStart"

        val enabled get() = configuration.getBoolean("$autoStart.enabled")
        val minimumPlayers get() = configuration.getInt("$autoStart.minimumPlayers")
        val rewards get() = configuration.getStringList("$autoStart.rewards") as List<String>
        val words get() = configuration.getStringList("$autoStart.words") as List<String>
    }
}