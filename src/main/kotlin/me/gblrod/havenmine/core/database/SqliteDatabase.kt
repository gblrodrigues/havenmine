package me.gblrod.havenmine.core.database

import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager

class SqliteDatabase(
    private val plugin: JavaPlugin
) {
    private val databaseFile = File(plugin.dataFolder, DATABASE_FILE_NAME)
    private val jdbcUrl: String
        get() = "JDBC:sqlite:${databaseFile.absolutePath.replace('\\', '/')}"

    fun initialize() {
        if (!plugin.dataFolder.exists() && !plugin.dataFolder.mkdirs()) {
            throw IOException("Could not create plugin data directory: ${plugin.dataFolder}")
        }

        Class.forName("org.sqlite.JDBC")

        openConnection().use { connection ->
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA journal_mode = WAL")
            }
        }
    }

    fun openConnection(): Connection {
        val connection = DriverManager.getConnection(jdbcUrl)

        try {
            connection.createStatement().use { statement ->
                statement.execute("PRAGMA busy_timeout = 5000")
            }

            return connection
        } catch (exception: Exception) {
            connection.close()
            throw exception
        }
    }

    private companion object {
        const val DATABASE_FILE_NAME = "havenmine.db"
    }
}