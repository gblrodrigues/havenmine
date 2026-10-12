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
    ),
    STARDUST(
        menuSlot = 12,
        configKey = "stardust",
        displayName = "Stardust",
        particle = Particle.END_ROD,
        icon = Material.END_ROD
    ),
    FROST(
        menuSlot = 13,
        configKey = "frost",
        displayName = "Frost",
        particle = Particle.SNOWFLAKE,
        icon = Material.SNOWBALL
    ),
    CHERRY(
        menuSlot = 14,
        configKey = "cherry",
        displayName = "Blossom",
        particle = Particle.CHERRY_LEAVES,
        icon = Material.CHERRY_SAPLING
    ),
    NATURE(
        menuSlot = 15,
        configKey = "nature",
        displayName = "Nature",
        particle = Particle.HAPPY_VILLAGER,
        icon = Material.EMERALD
    ),
    CLOUD(
        menuSlot = 16,
        configKey = "cloud",
        displayName = "Nimbus",
        particle = Particle.CLOUD,
        icon = Material.WHITE_WOOL
    )
}