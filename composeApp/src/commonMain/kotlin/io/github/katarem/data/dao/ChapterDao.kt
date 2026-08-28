package io.github.katarem.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.katarem.data.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLastRead(chapter: ChapterEntity)
    @Transaction
    @Query("SELECT * FROM chapters WHERE mangaId = :mangaId")
    suspend fun getChapterByMangaId(mangaId: String): List<ChapterEntity>

    @Delete
    suspend fun delete(chapter: ChapterEntity)
    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun get(id: String): ChapterEntity?
    @Query("SELECT * FROM chapters WHERE offline = 1")
    fun getOfflineChapters(): Flow<List<ChapterEntity>>

    @Transaction
    @Query("DELETE FROM chapters")
    fun deleteAll()

}