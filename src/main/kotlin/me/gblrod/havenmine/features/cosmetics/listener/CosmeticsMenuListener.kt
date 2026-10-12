package me.gblrod.havenmine.features.cosmetics.listener

import me.gblrod.havenmine.core.feedback.FeedbackSounds
import me.gblrod.havenmine.core.service.PlayerFeedbackService
import me.gblrod.havenmine.features.cosmetics.config.CosmeticMessageKeys
import me.gblrod.havenmine.features.cosmetics.config.InventoryConfig
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuHolder
import me.gblrod.havenmine.features.cosmetics.gui.CosmeticsMenuService
import me.gblrod.havenmine.features.cosmetics.model.CosmeticsMenuType
import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import me.gblrod.havenmine.features.particles.movement.service.MovementTrailService
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.plugin.java.JavaPlugin

class CosmeticsMenuListener(
    private val plugin: JavaPlugin,
    private val cosmeticsMenuService: CosmeticsMenuService,
    private val movementTrailService: MovementTrailService,
    private val playerFeedbackService: PlayerFeedbackService
) : Listener {
    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val topInventory = event.view.topInventory
        val holder = topInventory.holder as? CosmeticsMenuHolder ?: return

        val clickedInventory = event.clickedInventory

        if (clickedInventory == topInventory) {
            event.isCancelled = true
        } else {
            if (event.isShiftClick || event.click == ClickType.DOUBLE_CLICK) {
                event.isCancelled = true
            }

            return
        }

        val player = event.whoClicked as? Player ?: return

        if (event.rawSlot == InventoryConfig.CLOSE_MENU_SLOT) {
            plugin.server.scheduler.runTask(
                plugin,
                Runnable {
                    if (!player.isOnline) return@Runnable

                    cosmeticsMenuService.closeMenu(player)

                    playerFeedbackService.send(
                        sender = player,
                        sound = FeedbackSounds.CLOSE_MENU
                    )
                }
            )

            return
        }

        when (holder.type) {
            CosmeticsMenuType.MAIN -> {
                if (event.rawSlot != InventoryConfig.MOVEMENT_TRAILS_SLOT) {
                    return
                }

                playerFeedbackService.send(
                    sender = player,
                    sound = FeedbackSounds.OPEN_MENU
                )

                plugin.server.scheduler.runTask(
                    plugin,
                    Runnable {
                        if (player.isOnline) {
                            cosmeticsMenuService.openMovementTrails(player)
                        }
                    }
                )
            }

            CosmeticsMenuType.MOVEMENT_TRAILS -> {
                val trail = MovementTrailType.entries
                    .firstOrNull { it.menuSlot == event.rawSlot }
                    ?: return

                val equipped = movementTrailService.toggleTrail(
                    player = player,
                    trail = trail
                )

                playerFeedbackService.send(
                    sender = player,
                    messageKey = if (equipped) {
                        CosmeticMessageKeys.COSMETIC_EQUIPPED
                    } else {
                        CosmeticMessageKeys.COSMETIC_REMOVED
                    },
                    sound = if (equipped) {
                        FeedbackSounds.EQUIPPED
                    } else {
                        FeedbackSounds.DISABLED
                    },
                    placeholders = mapOf("cosmetic" to trail.displayName)
                )

                cosmeticsMenuService.refreshMovementTrailItems(
                    inventory = topInventory,
                    player = player
                )
            }
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (event.view.topInventory.holder !is CosmeticsMenuHolder) {
            return
        }

        val topInventorySize = event.view.topInventory.size

        if (event.rawSlots.any { it < topInventorySize }) {
            event.isCancelled = true
        }
    }
}