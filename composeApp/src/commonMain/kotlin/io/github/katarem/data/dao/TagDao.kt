package io.github.katarem.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.entity.TagEntity
import io.github.katarem.data.entity.TagWithMangas
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Transaction
    @Query("SELECT * FROM tags WHERE id = :tagId")
    fun getMangasWithTagsByTagId(tagId: String): Flow<TagWithMangas>

    @Transaction
    @Query("SELECT * FROM tags")
    fun getAll(): Flow<List<TagEntity>>

    @Upsert
    suspend fun upsert(mangas: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWithMangas(mangas: List<MangaTagEntity>)

    @Transaction
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getWithMangas(): Flow<List<TagWithMangas>>

    @Transaction
    @Query("DELETE FROM tags")
    suspend fun deleteAll()

    @Transaction
    @Query("DELETE FROM mangas_tags")
    suspend fun deleteAllAssociations()
}