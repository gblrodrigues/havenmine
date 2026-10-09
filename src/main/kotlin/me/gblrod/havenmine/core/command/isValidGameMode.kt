package me.gblrod.havenmine.core.command

import me.gblrod.havenmine.core.command.keys.CommandMessageKeys
import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import org.bukkit.GameMode
import org.bukkit.entity.Player

internal fun isValidGameMode(
    player: Player,
    requireSurvivalOrAdventure: Boolean = true,
    playerFeedbackService: PlayerFeedbackService
): Boolean {
    if (!requireSurvivalOrAdventure) {
        return true
    }

    val isAllowed = player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE

    if (!isAllowed) {
        playerFeedbackService.send(
            sender = player,
            messageKey = CommandMessageKeys.INVALID_GAME_MODE,
            sound = FeedbackSounds.ERROR
        )
    }

    return isAllowed
}