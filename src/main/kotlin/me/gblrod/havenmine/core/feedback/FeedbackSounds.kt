package me.gblrod.havenmine.core.feedback

import me.gblrod.havenmine.core.feedback.model.FeedbackSound
import org.bukkit.Sound

object FeedbackSounds {
    val SUCCESS = FeedbackSound(
        sound = Sound.BLOCK_NOTE_BLOCK_BELL,
        pitch = 0.5f
    )

    val ERROR = FeedbackSound(
        sound = Sound.BLOCK_NOTE_BLOCK_BASS,
        pitch = 0.5f
    )

    val DISABLED = FeedbackSound(
        sound = Sound.BLOCK_COMPARATOR_CLICK,
        pitch = 0.5f
    )

    val EQUIPPED = FeedbackSound(
        sound = Sound.BLOCK_NOTE_BLOCK_PLING,
        pitch = 2f
    )

    val OPEN_MENU = FeedbackSound(
        sound = Sound.BLOCK_NOTE_BLOCK_PLING,
        pitch = 1f
    )

    val CLOSE_MENU = FeedbackSound(
        sound = Sound.BLOCK_GRAVEL_BREAK,
        pitch = 1f
    )
}