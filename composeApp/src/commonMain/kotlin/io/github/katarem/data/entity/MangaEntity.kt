package io.github.katarem.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mangas")
data class MangaEntity(
    @PrimaryKey val id: String,
    val title: String,
    val coverArt: String,
    val description: String,
    val language: String,
    val currentChapterIndex: Int = 0,
    val liked: Boolean = false,
    val updatedAt: Long,
    val offline: Boolean = false,
    val read: Boolean = false,
)