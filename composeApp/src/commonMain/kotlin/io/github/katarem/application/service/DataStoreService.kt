package io.github.katarem.application.service

import io.github.katarem.data.entity.ChapterEntity
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow

interface DataStoreService {
    suspend fun upsertManga(manga: MangaEntity)
    suspend fun upsertMangas(mangas: List<MangaEntity>)
    suspend fun getManga(id: String): MangaEntity?
    fun getAllMangasWithChapters(): Flow<List<MangaWithChaptersEntity>>
    fun getAllMangas(): Flow<List<MangaEntity>>
    suspend fun upsertChapter(chapter: ChapterEntity)
    suspend fun getChapter(id: String): ChapterEntity?

    suspend fun getChapterByManga(mangaId: String): List<ChapterEntity>
    fun getOfflineChapters(): Flow<List<ChapterEntity>>

    fun getMangasByTagId(tagId: String): Flow<List<MangaEntity>>
    fun getTags(): Flow<List<TagEntity>>

    fun getMangaTags(): Flow<List<TagWithMangas>>

    suspend fun upsertTagsWithMangas(tags: List<MangaTagEntity>)
    suspend fun upsertTags(tags: List<TagEntity>)
    suspend fun clearAll()

}