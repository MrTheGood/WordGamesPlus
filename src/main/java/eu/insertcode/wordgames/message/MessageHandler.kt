package eu.insertcode.wordgames.message

import eu.insertcode.wordgames.config.ConfigManager.messagesConfig
import eu.insertcode.wordgames.config.Messages
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

/**
 * Created by maartendegoede on 18/05/2020.
 * Copyright © 2020 insertCode.eu. All rights reserved.
 */
object MessageHandler {

    fun sendMessage(sender: CommandSender, messagePath: String, coloured: Boolean = true) {
        var messages =
            if (messagesConfig.isList(messagePath)) messagesConfig.getStringList(messagePath)
            else listOf(messagesConfig.getString(messagePath))

        messages = messages.map {
            it.replace("{plugin}", messagesConfig.getString(Messages.Variables.plugin)!!).let {
                if (coloured) ChatColor.translateAlternateColorCodes('&', it)
                else it
            }
        }

        sender.sendMessage(messages.toTypedArray())
    }

    fun getMessages(path: String): List<String> {
        val messages: List<String> =
            if (messagesConfig.isList(path)) messagesConfig.getStringList(path)
            else listOf(messagesConfig.getString(path)!!)

        return messages.map { it.replace("{plugin}", messagesConfig.getString("variables.plugin")!!) }
    }

    fun getColouredMessages(path: String) =
        getMessages(path).map { ChatColor.translateAlternateColorCodes('&', it) }
}