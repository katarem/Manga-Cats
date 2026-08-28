package io.github.katarem.application.repository

import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import kotlinx.coroutines.flow.Flow

interface MangaRepository {

    fun getAllWithChapter(): Flow<List<MangaWithChaptersEntity>>
    suspend fun upsert(manga: MangaEntity)
    suspend fun upsert(mangas: List<MangaEntity>)
    suspend fun get(id: String): MangaEntity?
    fun getAll(): Flow<List<MangaEntity>>

    suspend fun deleteAll()

}