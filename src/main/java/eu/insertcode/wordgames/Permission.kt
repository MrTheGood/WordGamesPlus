package eu.insertcode.wordgames

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

enum class Permission(private val permission: String) {
    STOP("wordgames.stop"),
    LIST("wordgames.list"),
    RELOAD("wordgames.reload"),
    UPDATE("wordgames.update"),

    START_ALL("wordgames.start"),
    START_HOVER("wordgames.start.hover"),
    START_FIRST("wordgames.start.first"),
    START_TIMED("wordgames.start.timed"),
    START_UNMUTE("wordgames.start.unmute"),
    START_REORDER("wordgames.start.reorder"),
    START_CALCULATE("wordgames.start.calculate"),

    START_DISABLED("wordgames.start.disabled"),

    PLAY_ALL("wordgames.play"),
    PLAY_HOVER("wordgames.play.hover"),
    PLAY_TIMED("wordgames.play.timed"),
    PLAY_UNMUTE("wordgames.play.unmute"),
    PLAY_FIRST("wordgames.play.first"),
    PLAY_REORDER("wordgames.play.reorder"),
    PLAY_CALCULATE("wordgames.play.calculate");

    fun forSender(s: CommandSender, alternative: Permission? = null): Boolean =
        s.hasPermission(permission) || alternative?.forSender(s) ?: false

    fun forPlayer(p: Player, alternative: Permission? = null): Boolean =
        p.hasPermission(permission) || alternative?.forPlayer(p) ?: false
}