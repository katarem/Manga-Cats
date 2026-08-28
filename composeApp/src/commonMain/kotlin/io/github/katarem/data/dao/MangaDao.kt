package io.github.katarem.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import io.github.katarem.data.entity.MangaEntity
import io.github.katarem.data.entity.MangaWithChaptersEntity
import io.github.katarem.data.entity.MangaWithTags
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow

@Dao
interface MangaDao {
    @Transaction
    @Query("SELECT * FROM mangas")
    fun getAllWithChapters(): Flow< List<MangaWithChaptersEntity>>
    @Transaction
    @Query("SELECT * FROM mangas ORDER BY updatedAt DESC")
    fun getAll(): Flow< List<MangaEntity>>

    @Upsert
    suspend fun upsert(mangas: List<MangaEntity>)
    @Delete
    suspend fun delete(manga: MangaEntity)
    @Query("SELECT * FROM mangas WHERE id = :id")
    suspend fun getById(id: String): MangaEntity?

    @Transaction
    @Query("DELETE FROM mangas")
    suspend fun clearAll()

    @Transaction
    @Query("SELECT * FROM mangas")
    fun getMangasWithTags(): Flow<List<MangaWithTags>>

}