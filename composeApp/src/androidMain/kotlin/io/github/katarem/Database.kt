package io.github.katarem

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.katarem.data.AppDatabase

fun getDatabaseBuilder(ctx: Context): AppDatabase {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("manga.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).fallbackToDestructiveMigration(true).build()
}

class DatabaseProvider(private val context: Context) {
    val database: AppDatabase by lazy {
        getDatabaseBuilder(context)
    }
}