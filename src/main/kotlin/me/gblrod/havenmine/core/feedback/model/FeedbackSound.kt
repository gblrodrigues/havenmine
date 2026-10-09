package me.gblrod.havenmine.core.feedback.model

import org.bukkit.Sound

data class FeedbackSound(
    val sound: Sound,
    val volume: Float = 1f,
    val pitch: Float = 1f
)