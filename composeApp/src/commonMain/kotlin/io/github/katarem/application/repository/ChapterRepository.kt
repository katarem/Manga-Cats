package io.github.katarem.application.repository

import io.github.katarem.data.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

interface ChapterRepository {
    suspend fun upsert(chapter: ChapterEntity)
    suspend fun delete(chapter: ChapterEntity)
    suspend fun get(id: String): ChapterEntity?
    suspend fun getByMangaId(mangaId: String): List<ChapterEntity>
    fun getOfflineChapters(): Flow<List<ChapterEntity>>
    suspend fun deleteAll()

}