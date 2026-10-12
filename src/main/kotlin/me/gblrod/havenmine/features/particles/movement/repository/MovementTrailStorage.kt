package me.gblrod.havenmine.features.particles.movement.repository

import me.gblrod.havenmine.core.database.SqliteDatabase
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

class MovementTrailStorage(private val plugin: JavaPlugin) : AutoCloseable {
    private var movementTrailRepository: MovementTrailRepository? = null

    fun initialize(): MovementTrailRepository? {
        return try {
            val database = SqliteDatabase(plugin = plugin)
            database.initialize()

            SqliteMovementTrailRepository(
                plugin = plugin,
                database = database
            ).also { movementTrailRepository = it }

        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to initialize SQLite storage.", e)
            null
        }
    }

    override fun close() {
        movementTrailRepository?.close()
        movementTrailRepository = null
    }
}