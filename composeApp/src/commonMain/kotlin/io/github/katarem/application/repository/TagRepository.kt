package io.github.katarem.application.repository

import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    suspend fun upsertWithMangas(tags: List<MangaTagEntity>)
    suspend fun upsert(tags: List<TagEntity>)
    fun getWithMangas(): Flow<List<TagWithMangas>>
    fun getAll(): Flow<List<TagEntity>>
    fun getByTagId(tagId: String): Flow<TagWithMangas>
    suspend fun deleteAll()
    suspend fun deleteAssociations()
}