package io.github.katarem

import androidx.room.Room
import androidx.sqlite.SQLiteDriver
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.katarem.data.AppDatabase
import java.io.File

fun getAppDatabase(): AppDatabase {
    val dbFile = File(System.getProperty("user.home"), "manga.db")
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
        .fallbackToDestructiveMigration(true).build()
}