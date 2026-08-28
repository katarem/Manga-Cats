package io.github.katarem.application.service

import io.github.katarem.data.dto.ChapterDTO
import io.github.katarem.data.dto.MangaDTO
import io.github.katarem.data.dto.TagDTO
import io.github.katarem.data.dto.response.MangaPagedResponse
import io.github.katarem.data.model.MangaQuery
import kotlinx.coroutines.flow.Flow

interface MangaService {
    suspend fun searchMangas(searchParams: MangaQuery): List<MangaDTO>
    suspend fun getChapters(id: String, language: String = "en"): List<ChapterDTO>
    suspend fun getPages(chapterId: String, saver: Boolean = false): List<String>
    suspend fun getManga(id: String): MangaDTO?

    suspend fun getCover(mangaId: String, filename: String): ByteArray?

    suspend fun searchMangasAdvanced(searchParams: MangaQuery, max: Int = 0): Flow<List<MangaDTO>>

    suspend fun getTags(): List<TagDTO>

}