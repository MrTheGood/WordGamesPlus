package eu.insertcode.wordgames.util

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

/**
 * Created by maartendegoede on 27/10/2018.
 * Copyright © 2018 insertCode.eu. All rights reserved.
 */
inline fun <reified T : Event> JavaPlugin.listenFor(
        listener: Listener = object : Listener {},
        priority: EventPriority = EventPriority.NORMAL,
        crossinline callback: (t: T) -> Unit
) {
    server.pluginManager.registerEvent(
            T::class.java,
            listener,
            priority,
            { _, event -> callback(event as T) },
            this
    )
}