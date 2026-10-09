package me.gblrod.havenmine.core.command

import java.util.*

object CommandCooldownManager {
    private val cooldowns = mutableMapOf<Pair<UUID, String>, Long>()

    fun startCooldown(playerId: UUID, command: String, durationSeconds: Long) {
        val key = playerId to command.lowercase()
        cooldowns[key] = System.currentTimeMillis() + durationSeconds * 1000
    }

    fun getRemaining(playerId: UUID, command: String): Long {
        val key = playerId to command.lowercase()
        val expiresAt = cooldowns[key] ?: return 0L
        val remainingMillis = expiresAt - System.currentTimeMillis()

        if (remainingMillis <= 0) {
            cooldowns.remove(key)
            return 0L
        }

        return (remainingMillis + 999) / 1000
    }

    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60

        return when {
            minutes > 0 && remainingSeconds > 0 -> "${minutes}m ${remainingSeconds}s"
            minutes > 0 -> "${minutes}m"
            else -> "${remainingSeconds}s"
        }
    }
}