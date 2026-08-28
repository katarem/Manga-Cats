package io.github.katarem.application.repository

import io.github.katarem.data.AppDatabase
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow

class TagRepositoryImpl(
    private val db: AppDatabase,
) : TagRepository {
    override suspend fun upsertWithMangas(tags: List<MangaTagEntity>) {
        db.tagDao().upsertWithMangas(tags)
    }

    override suspend fun upsert(tags: List<TagEntity>) {
        db.tagDao().upsert(tags)
    }

    override fun getWithMangas(): Flow<List<TagWithMangas>> {
        return db.tagDao().getWithMangas()
    }

    override fun getAll(): Flow<List<TagEntity>> {
        return db.tagDao().getAll()
    }

    override fun getByTagId(tagId: String): Flow<TagWithMangas> {
        return db.tagDao().getMangasWithTagsByTagId(tagId)
    }

    override suspend fun deleteAll() {
        return db.tagDao().deleteAll()
    }

    override suspend fun deleteAssociations() {
        return db.tagDao().deleteAllAssociations()
    }
}