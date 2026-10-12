package me.gblrod.havenmine.features.particles.movement.model

import org.bukkit.Material
import org.bukkit.Particle

enum class MovementTrailType(
    val menuSlot: Int,
    val configKey: String,
    val displayName: String,
    val particle: Particle,
    val icon: Material
) {
    FLAME(
        displayName = "Fire",
        configKey = "fire",
        particle = Particle.FLAME,
        icon = Material.BLAZE_POWDER,
        menuSlot = 10
    ),
    WITCH(
        displayName = "Magic",
        configKey = "magic",
        particle = Particle.WITCH,
        icon = Material.AMETHYST_SHARD,
        menuSlot = 11
    )
}