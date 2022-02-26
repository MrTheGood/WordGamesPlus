package eu.insertcode.wordgames.config

/**
 * Created by maartendegoede on 18/05/2020.
 * Copyright © 2020 insertCode.eu. All rights reserved.
 */
object Messages {
    const val reload = "reload"

    object Variables {
        private const val variables = "variables"

        const val plugin = "$variables.plugin"
        const val hover = "$variables.HOVER"
    }

    object Games {
        private const val games = "games"

        const val autoStop = "$games.autoStop"
        const val reorder = "$games.reorder"
        const val first = "$games.first"
        const val unmute = "$games.unmute"
        const val hover = "$games.hover"

        object Timed {
            private const val timed = "timed"

            const val start = "$games.$timed.start"
            const val gameWon = "$games.$timed.gameWon"
            const val stop = "$games.$timed.stop"
        }

        const val calculate = "$games.calculate"

        object List {
            private const val list = "list"

            const val prefix = "$games.$list.prefix"
            const val suffix = "$games.$list.suffix"
        }

        const val gameWon = "$games.gameWon"
        const val stop = "$games.stop"
    }

    object Error {
        private const val error = "error"

        const val gameDisabled = "$error.gameDisabled"
        const val noGamesEnabled = "$error.noGamesEnabled"
        const val noPlayPermissions = "$error.noPlayPermissions"
        const val noPermissions = "$error.noPermissions"
        const val typeNotFound = "$error.typeNotFound"
        const val wrongInput = "$error.wrongInput"
        const val notPlaying = "$error.notPlaying"
        const val configWrong = "$error.configWrong"
        const val tooManyGames = "$error.tooManyGames"
    }
}