package eu.insertcode.wordgames.util

import com.google.gson.JsonParser
import eu.insertcode.wordgames.Permission
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor.DARK_GREEN
import org.bukkit.ChatColor.GREEN
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL

const val RESOURCE_ID = "33080"

private fun JavaPlugin.onUpdate(callback: (version: String) -> Unit) = Thread {
    try {
        val base = "https://api.spiget.org/v2/resources/"
        val conn = URL("$base$RESOURCE_ID/versions?size=1&sort=-releaseDate&fields=name").openConnection()
        val json = InputStreamReader(conn.inputStream).let { JsonParser().parse(it).asJsonArray }

        val version = json.first().asJsonObject["name"].asString
        if (newest(version, description.version) != description.version)
            callback(version)
    } catch (e: IOException) {
    }
}.start()


fun JavaPlugin.checkUpdate() {
    onUpdate { version ->
        val url = "https://www.spigotmc.org/resources/$RESOURCE_ID"
        val message = TextComponent(
                "${GREEN}Update version $version is available for $DARK_GREEN${description.name}$GREEN!\nDownload it here: $DARK_GREEN$url"
        ).apply {
            clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
        }

        server.scheduler.runTask(this) {
            server.consoleSender.sendMessage(message.text)
        }

        listenFor<PlayerJoinEvent> { event ->
            event.player.apply {
                if (Permission.UPDATE.forPlayer(player)) {
                    player.sendMessage(message.text)
                }
            }
        }
    }
}

fun newest(v1: String, v2: String): String {
    val d1 = v1.split('.')
    val d2 = v2.split('.')
    for (i in 0..Math.max(d1.size, d2.size)) {
        if (!(d1[i].isInt() && d2[i].isInt())) continue
        if (i !in d1.indices) return v1
        if (i !in d2.indices) return v2
        if (d1[i].toInt() > d2[i].toInt()) return v2
        if (d1[i].toInt() < d2[i].toInt()) return v1
    }
    return v1
}

fun String.isInt() =
        try {
            toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }