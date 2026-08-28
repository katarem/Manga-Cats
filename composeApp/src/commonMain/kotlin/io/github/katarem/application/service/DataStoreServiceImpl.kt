package io.github.katarem.application.service

import io.github.katarem.application.repository.ChapterRepository
import io.github.katarem.application.repository.MangaRepository
import io.github.katarem.application.repository.TagRepository
import io.github.katarem.data.entity.ChapterEntity
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreServiceImpl(
    private val mangaRepository: MangaRepository,
    private val chapterRepository: ChapterRepository,
    private val tagRepository: TagRepository,
) : DataStoreService {

    override suspend fun upsertManga(manga: MangaEntity) {
        mangaRepository.upsert(manga)
    }

    override suspend fun upsertMangas(mangas: List<MangaEntity>) {
        mangaRepository.upsert(mangas)
    }

    override suspend fun getManga(id: String): MangaEntity? {
        return mangaRepository.get(id)
    }

    override fun getAllMangasWithChapters(): Flow<List<MangaWithChaptersEntity>> {
        return mangaRepository.getAllWithChapter()
    }

    override fun getAllMangas(): Flow<List<MangaEntity>> {
        return mangaRepository.getAll()
    }

    override suspend fun upsertChapter(chapter: ChapterEntity) {
        chapterRepository.upsert(chapter)
    }

    override suspend fun getChapter(id: String): ChapterEntity? {
        return chapterRepository.get(id)
    }

    override suspend fun getChapterByManga(mangaId: String): List<ChapterEntity> {
        return chapterRepository.getByMangaId(mangaId)
    }

    override fun getOfflineChapters(): Flow<List<ChapterEntity>> {
        return chapterRepository.getOfflineChapters()
    }

    override fun getMangasByTagId(tagId: String): Flow<List<MangaEntity>> {
        return tagRepository.getByTagId(tagId).map { it.mangas }
    }

    override fun getTags(): Flow<List<TagEntity>> {
        return tagRepository.getAll()
    }

    override fun getMangaTags(): Flow<List<TagWithMangas>> {
        return tagRepository.getWithMangas()
    }

    override suspend fun upsertTagsWithMangas(tags: List<MangaTagEntity>) {
        tagRepository.upsertWithMangas(tags)
    }

    override suspend fun upsertTags(tags: List<TagEntity>) {
        tagRepository.upsert(tags)
    }

    override suspend fun clearAll() {
        chapterRepository.deleteAll()
        tagRepository.deleteAssociations()
        mangaRepository.deleteAll()
        tagRepository.deleteAll()
    }


}