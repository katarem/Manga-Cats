package io.github.katarem.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import io.github.katarem.data.dao.ChapterDao
import io.github.katarem.data.dao.MangaDao
import io.github.katarem.data.dao.TagDao
import io.github.katarem.data.entity.ChapterEntity
import io.github.katarem.data.entity.ChapterWithPagesEntity
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import io.github.katarem.data.entity.PageEntity
import io.github.katarem.data.entity.TagEntity

@Database(entities = [MangaEntity::class, ChapterEntity::class, PageEntity::class, MangaTagEntity::class, TagEntity::class], version = 15)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mangaDao(): MangaDao
    abstract fun chapterDao(): ChapterDao
    abstract fun tagDao(): TagDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
