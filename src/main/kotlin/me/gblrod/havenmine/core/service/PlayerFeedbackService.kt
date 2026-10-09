package me.gblrod.havenmine.core.service

import me.gblrod.havenmine.core.feedback.model.FeedbackSound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PlayerFeedbackService(
    private val messageService: MessageService
) {
    fun send(
        sender: CommandSender,
        messageKey: String,
        sound: FeedbackSound? = null,
        placeholders: Map<String, String> = emptyMap()
    ) {
        val message = if (placeholders.isEmpty()) {
            messageService.get(messageKey)
        } else {
            messageService.get(path = messageKey, placeholders = placeholders)
        }

        sender.sendMessage(message)

        if (sender is Player) {
            sound?.let {
                sender.playSound(sender.location, it.sound, it.volume, it.pitch)
            }
        }
    }
}