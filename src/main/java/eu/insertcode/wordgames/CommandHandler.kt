package eu.insertcode.wordgames

import eu.insertcode.wordgames.config.Config
import eu.insertcode.wordgames.config.Config.GameOptions.maxPlayingGames
import eu.insertcode.wordgames.config.ConfigManager.reload
import eu.insertcode.wordgames.config.Messages
import eu.insertcode.wordgames.games.*
import eu.insertcode.wordgames.message.MessageHandler.getColouredMessages
import eu.insertcode.wordgames.message.MessageHandler.sendMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class CommandHandler internal constructor(private val plugin: Main) : CommandExecutor {
    override fun onCommand(s: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        try {
            if (args[0].equals("help", ignoreCase = true)) return onCommandHelp(s)
            if (args[0].equals("list", ignoreCase = true)) return onCommandList(s)
            if (args[0].equals("stop", ignoreCase = true)) return onCommandStop(s)
            if (args[0].equals("reload", ignoreCase = true)) return onCommandReload(s)

            /*
			 * wordgames <type> <word> [amount] <reward>
			 */
            if (args.size >= 2) {
                if (plugin.wordGames.size >= maxPlayingGames && maxPlayingGames > 0)
                    return errorMessage(s, Messages.Error.tooManyGames)

                //Test which wordgame the user is trying to create
                if (args[0].equals("calculate", ignoreCase = true)) {
                    val enabled = Config.GameOptions.Calculate.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s))
                        return errorMessage(s, Messages.Error.gameDisabled)

                    // wordgames <type> [amount] <reward>
                    return if (Permission.START_ALL.forSender(s, Permission.START_CALCULATE))
                        createCalculateGame(s, args)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                if (args[0].equals("first", ignoreCase = true)) {
                    val enabled = Config.GameOptions.First.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s))
                        return errorMessage(s, Messages.Error.gameDisabled)

                    // wordgames <type> [amount] <reward>
                    return if (Permission.START_ALL.forSender(s, Permission.START_FIRST))
                        createGame(s, args, Type.FIRST)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                if (args[0].equals("hover", ignoreCase = true)) {
                    val enabled = Config.GameOptions.Hover.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s)) return errorMessage(s, Messages.Error.gameDisabled)

                    return if (Permission.START_ALL.forSender(s, Permission.START_HOVER))
                        createGame(s, args, Type.HOVER)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                if (args[0].equals("reorder", ignoreCase = true)) {
                    val enabled = Config.GameOptions.Reorder.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s))
                        return errorMessage(s, Messages.Error.gameDisabled)

                    return if (Permission.START_ALL.forSender(s, Permission.START_REORDER))
                        createGame(s, args, Type.REORDER)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                if (args[0].equals("unmute", ignoreCase = true)) {
                    val enabled = Config.GameOptions.Unmute.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s))
                        return errorMessage(s, Messages.Error.gameDisabled)

                    return if (Permission.START_ALL.forSender(s, Permission.START_UNMUTE))
                        createGame(s, args, Type.UNMUTE)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                if (args[0].equals("timed", ignoreCase = true)) {
                    val enabled = Config.GameOptions.Timed.enabled
                    if (!enabled && !Permission.START_DISABLED.forSender(s))
                        return errorMessage(s, Messages.Error.gameDisabled)

                    return if (Permission.START_ALL.forSender(s, Permission.START_TIMED))
                        createGame(s, args, Type.TIMED)
                    else errorMessage(s, Messages.Error.noPermissions)
                }

                return errorMessage(s, Messages.Error.typeNotFound)
            }
        } catch (e: IndexOutOfBoundsException) {
            return onCommandHelp(s)
        }
        return onCommandHelp(s)
    }

    private fun onCommandHelp(s: CommandSender): Boolean {
        s.sendMessage("$GREEN/wordgames help$DARK_GREEN  to show this message.")
        if (Permission.RELOAD.forSender(s))
            s.sendMessage("$GREEN/wordgames reload$DARK_GREEN  to reload the configuration.")
        if (Permission.LIST.forSender(s))
            s.sendMessage("$GREEN/wordgames list$DARK_GREEN  To see all currently playing games.")
        if (Permission.STOP.forSender(s))
            s.sendMessage("$GREEN/wordgames stop$DARK_GREEN  to stop any and all playing games.")
        if (Permission.START_ALL.forSender(s, Permission.START_REORDER))
            s.sendMessage("$GREEN/wordgames reorder <word> [number] <reward>$DARK_GREEN  to start the 'reorder' mini game.")
        if (Permission.START_ALL.forSender(s, Permission.START_FIRST))
            s.sendMessage("$GREEN/wordgames first <word> [number] <reward>$DARK_GREEN  to start the 'first' mini game.")
        if (Permission.START_ALL.forSender(s, Permission.START_HOVER))
            s.sendMessage("$GREEN/wordgames hover <word> [number] <reward>$DARK_GREEN to start the 'hover' mini game.")
        if (Permission.START_ALL.forSender(s, Permission.START_UNMUTE))
            s.sendMessage("$GREEN/wordgames unmute <word> [number] <reward>$DARK_GREEN to start the 'unmute' mini game.")
        if (Permission.START_ALL.forSender(s, Permission.START_TIMED))
            s.sendMessage("$GREEN/wordgames timed <word> [number] <reward>$DARK_GREEN to start the 'timed' mini game.")
        if (Permission.START_ALL.forSender(s, Permission.START_CALCULATE))
            s.sendMessage("$GREEN/wordgames calculate [number] <reward>$DARK_GREEN to start the 'calculate' mini game.")

        s.sendMessage("$GOLD[${DARK_RED}WordGames+$GOLD]$DARK_GREEN Plugin version: ${plugin.description.version}")
        return true
    }

    private fun onCommandList(s: CommandSender): Boolean {
        if (!Permission.LIST.forSender(s))
            return errorMessage(s, Messages.Error.noPermissions)

        sendMessage(s, Messages.Games.List.prefix, true)
        for (i in plugin.wordGames.indices) {
            s.sendMessage("$DARK_GREEN$i.")
            for (message in plugin.wordGames[i].gameMessages) {
                s.sendMessage("$DARK_GREEN   $message")
            }
        }
        sendMessage(s, Messages.Games.List.suffix, true)
        return true
    }

    private fun onCommandStop(s: CommandSender): Boolean {
        if (!Permission.STOP.forSender(s))
            return errorMessage(s, Messages.Error.noPermissions)


        if (plugin.wordGames.isEmpty())
            return errorMessage(s, Messages.Error.notPlaying)


        for (game in plugin.wordGames) game.endGame()
        plugin.wordGames = arrayListOf()

        for (msg in getColouredMessages(Messages.Games.stop)) Bukkit.broadcastMessage(msg)

        return true
    }

    private fun onCommandReload(s: CommandSender): Boolean {
        if (!Permission.RELOAD.forSender(s))
            return errorMessage(s, Messages.Error.noPermissions)


        reload(plugin)
        sendMessage(s, Messages.reload, true)
        return true
    }


    private fun errorMessage(s: CommandSender, path: String) =
        true.also { sendMessage(s, path, true) }


    private fun createGame(s: CommandSender, args: Array<String>, type: Type): Boolean {
        val rewardString: String
        val amount: Int

        if (args.size < 4) {
            rewardString = args[2]
            amount = 1
        } else {
            rewardString = args[3]
            amount = try {
                args[2].toInt()
            } catch (e: NumberFormatException) {
                return errorMessage(s, Messages.Error.wrongInput)
            }
        }
        val reward = Reward(amount, rewardString)
        val wordToType = args[1]

        when (type) {
            Type.REORDER -> plugin.wordGames.add(ReorderGame(plugin, wordToType, reward))
            Type.FIRST -> plugin.wordGames.add(FirstGame(plugin, wordToType, reward))
            Type.HOVER -> plugin.wordGames.add(HoverGame(plugin, wordToType, reward))
            Type.UNMUTE -> plugin.wordGames.add(UnmuteGame(plugin, wordToType, reward))
            Type.TIMED -> plugin.wordGames.add(TimedGame(plugin, wordToType, reward))
        }
        return true
    }


    private fun createCalculateGame(s: CommandSender, args: Array<String>): Boolean {
        val rewardString: String
        val amount: Int

        if (args.size < 3) {
            rewardString = args[1]
            amount = 1
        } else {
            rewardString = args[2]
            amount = try {
                args[1].toInt()
            } catch (e: NumberFormatException) {
                return errorMessage(s, Messages.Error.wrongInput)
            }
        }
        val reward = Reward(amount, rewardString)
        plugin.wordGames.add(CalculateGame(plugin, "", reward))

        return true
    }

    private enum class Type {
        HOVER, FIRST, REORDER, UNMUTE, TIMED
    }
}