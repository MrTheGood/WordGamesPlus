package eu.insertcode.wordgames.util

import com.google.gson.JsonParser
import eu.insertcode.wordgames.Permission
import org.bukkit.ChatColor.DARK_GREEN
import org.bukkit.ChatColor.GREEN
import org.bukkit.event.EventPriority
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
        val json = InputStreamReader(conn.inputStream).use { JsonParser.parseReader(it).asJsonArray }

        val version = json.first().asJsonObject["name"].asString
        if (version newerThan description.version)
            callback(version)
        else logger.info("Running latest version ${description.name}")
    } catch (e: IOException) {
        logger.warning("Failed to check for updates..")
    }
}.start()


fun JavaPlugin.checkUpdate() {
    onUpdate { version ->
        val url = "https://www.spigotmc.org/resources/$RESOURCE_ID"
        val message = "${GREEN}Update version $version is available for $DARK_GREEN${description.name}$GREEN!\nDownload it here: $DARK_GREEN$url"

        server.scheduler.runTask(this, Runnable {
            server.consoleSender.sendMessage(message)
        })

        listenFor<PlayerJoinEvent>(priority = EventPriority.MONITOR) { event ->
            event.player.run {
                if (Permission.UPDATE.forPlayer(player)) {
                    player?.sendMessage(message)
                }
            }
        }
    }
}

fun getVersionNumbers(ver: String) =
        ver.split(".").map { v ->
            try {
                v.toInt()
            } catch (e: NumberFormatException) {
                val v1 = v.replace("[^0123456789]".toRegex(), "")
                if (v1.isEmpty()) 0 else v1.toInt()
            }
        }

infix fun String.newerThan(baseFW: String) = false.also {
    val testVer = getVersionNumbers(this)
    val baseVer = getVersionNumbers(baseFW)

    for (i in testVer.indices)
        if (i in baseVer.indices && testVer[i] != baseVer[i])
            return testVer[i] > baseVer[i]
}