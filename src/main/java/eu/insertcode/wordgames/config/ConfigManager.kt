package eu.insertcode.wordgames.config

import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

/**
 * Created by maartendegoede on 04/03/2021.
 * Copyright © 2021 Maarten de Goede. All rights reserved.
 */
object ConfigManager {
    private lateinit var messagesFile: File
    lateinit var messagesConfig: FileConfiguration
        private set

    fun createFiles(plugin: JavaPlugin) {
        val configFile = File(plugin.dataFolder, "config.yml")
        messagesFile = File(plugin.dataFolder, "messages.yml")

        if (!configFile.exists()) {
            configFile.parentFile.mkdirs()
            plugin.saveResource("config.yml", false)
            Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] config.yml not found. Creating...")
        }

        if (!messagesFile.exists()) {
            messagesFile.parentFile.mkdirs()
            plugin.saveResource("messages.yml", false)
            Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] messages.yml not found. Creating...")
        }

        val config = YamlConfiguration()
        messagesConfig = YamlConfiguration()

        try {
            config.load(configFile)
            messagesConfig.load(messagesFile)
        } catch (e: IOException) {
            Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] Something went wrong while loading the config.")
            throw e
        } catch (e: InvalidConfigurationException) {
            Bukkit.getConsoleSender().sendMessage("[<WordGames+ by MrTheGood>] The config is wrong.")
            throw e
        }
    }

    fun reload(plugin: JavaPlugin) {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile)
        plugin.reloadConfig()
        Config.initialize(plugin.config)
    }
}