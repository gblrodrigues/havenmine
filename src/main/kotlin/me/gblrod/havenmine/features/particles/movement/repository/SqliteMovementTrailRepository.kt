package me.gblrod.havenmine.features.particles.movement.repository

import me.gblrod.havenmine.core.database.SqliteDatabase
import me.gblrod.havenmine.features.particles.movement.model.MovementTrailType
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.*
import java.util.logging.Level

class SqliteMovementTrailRepository(
    private val plugin: JavaPlugin,
    private val database: SqliteDatabase
) : MovementTrailRepository {

    private val databaseExecutor: ExecutorService =
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "HavenMine-SQLite").apply {
                isDaemon = true
            }
        }

    init {
        createTable()
    }

    override fun load(playerId: UUID): CompletableFuture<MovementTrailType?> {
        return CompletableFuture.supplyAsync(
            {
                executeSafely("load movement trail for $playerId") {
                    loadFromDatabase(playerId)
                }
            },
            databaseExecutor
        )
    }

    override fun save(playerId: UUID, trail: MovementTrailType?): CompletableFuture<Void> {
        return CompletableFuture.runAsync(
            {
                executeSafely("save movement trail for $playerId") {
                    saveToDatabase(playerId, trail)
                }
            },
            databaseExecutor
        )
    }

    private fun createTable() {
        database.openConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS movement_trail_selections (
                        player_uuid TEXT PRIMARY KEY NOT NULL,
                        trail_key TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }

    private fun loadFromDatabase(playerId: UUID): MovementTrailType? {
        val storedKey = database.openConnection().use { connection ->
            connection.prepareStatement(
                """
                SELECT trail_key
                FROM movement_trail_selections
                WHERE player_uuid = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())

                statement.executeQuery().use { result ->
                    if (result.next()) result.getString("trail_key") else null
                }
            }
        } ?: return null

        val trail = MovementTrailType.entries.firstOrNull {
            it.configKey == storedKey
        }

        if (trail != null) return trail

        plugin.logger.warning("Unknown saved movement trail '$storedKey' for player $playerId. Removing stale selection.")
        deleteStoredTrail(playerId, storedKey)

        return null
    }

    private fun saveToDatabase(playerId: UUID, trail: MovementTrailType?) {
        database.openConnection().use { connection ->
            if (trail == null) {
                connection.prepareStatement(
                    """
                    DELETE FROM movement_trail_selections
                    WHERE player_uuid = ?
                    """.trimIndent()
                ).use { statement ->
                    statement.setString(1, playerId.toString())
                    statement.executeUpdate()
                }

                return
            }

            connection.prepareStatement(
                """
                INSERT INTO movement_trail_selections (
                    player_uuid,
                    trail_key
                )
                VALUES (?, ?)
                ON CONFLICT(player_uuid)
                DO UPDATE SET trail_key = excluded.trail_key
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())
                statement.setString(2, trail.configKey)
                statement.executeUpdate()
            }
        }
    }

    private fun deleteStoredTrail(playerId: UUID, trailKey: String) {
        database.openConnection().use { connection ->
            connection.prepareStatement(
                """
                DELETE FROM movement_trail_selections
                WHERE player_uuid = ? AND trail_key = ?
                """.trimIndent()
            ).use { statement ->
                statement.setString(1, playerId.toString())
                statement.setString(2, trailKey)
                statement.executeUpdate()
            }
        }
    }

    private inline fun <T> executeSafely(operation: String, action: () -> T): T {
        try {
            return action()
        } catch (e: Exception) {
            plugin.logger.log(Level.SEVERE, "Failed to $operation.", e)

            throw CompletionException(e)
        }
    }

    override fun close() {
        databaseExecutor.shutdown()
        try {
            if (!databaseExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                plugin.logger.warning("SQLite operations did not finish within five seconds. " + "Forcing executor shutdown.")
                databaseExecutor.shutdownNow()
            }
        } catch (_: InterruptedException) {
            databaseExecutor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
}