package io.github.katarem.application.repository

import io.github.katarem.data.AppDatabase
import io.github.katarem.data.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

class ChapterRepositoryImpl(
    private val database: AppDatabase
) : ChapterRepository {
    override suspend fun upsert(chapter: ChapterEntity) {
        database.chapterDao().upsertLastRead(chapter)
    }

    override suspend fun delete(chapter: ChapterEntity) {
        database.chapterDao().delete(chapter)
    }

    override suspend fun get(id: String): ChapterEntity? {
        return database.chapterDao().get(id)
    }

    override suspend fun getByMangaId(mangaId: String): List<ChapterEntity> {
        return database.chapterDao().getChapterByMangaId(mangaId)
    }

    override fun getOfflineChapters(): Flow<List<ChapterEntity>> {
        return database.chapterDao().getOfflineChapters()
    }

    override suspend fun deleteAll() {
        database.chapterDao().deleteAll()
    }
}