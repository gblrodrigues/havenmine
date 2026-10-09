package me.gblrod.havenmine.core.service

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.FileConfiguration

class MessageService(
    private val config: FileConfiguration
) {
    private val miniMessage = MiniMessage.miniMessage()

    fun get(path: String): Component {
        val message = config.getString("messages.$path").orEmpty()

        return miniMessage.deserialize(message)
    }

    fun get(
        path: String,
        placeholders: Map<String, String>
    ): Component {
        var message = config.getString("messages.$path").orEmpty()

        for ((key, value) in placeholders) {
            message = message.replace(oldValue = "%$key%", newValue = value)
        }

        return miniMessage.deserialize(message)
    }
}