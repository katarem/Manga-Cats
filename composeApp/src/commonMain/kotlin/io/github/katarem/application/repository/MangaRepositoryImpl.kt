package io.github.katarem.application.repository

import io.github.katarem.data.AppDatabase
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import kotlinx.coroutines.flow.Flow

class MangaRepositoryImpl(
    private val database: AppDatabase
) : MangaRepository {

    override fun getAllWithChapter(): Flow<List<MangaWithChaptersEntity>> {
        return database.mangaDao().getAllWithChapters()
    }

    override suspend fun upsert(manga: MangaEntity) {
        database.mangaDao().upsert(listOf(manga))
    }

    override suspend fun upsert(mangas: List<MangaEntity>) {
        database.mangaDao().upsert(mangas)
    }

    override suspend fun get(id: String): MangaEntity? {
        return database.mangaDao().getById(id)
    }

    override fun getAll(): Flow<List<MangaEntity>> {
        return database.mangaDao().getAll()
    }

    override suspend fun deleteAll() {
        return database.mangaDao().clearAll()
    }


}