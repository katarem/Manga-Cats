package io.github.katarem.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapters",
    foreignKeys = [ForeignKey(
        entity = MangaEntity::class,
        parentColumns = ["id"],
        childColumns = ["mangaId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("mangaId")]
    )
data class ChapterEntity(
    @PrimaryKey val id: String,
    val title: String,
    val mangaId: String,
    val chapterNumber: String,
    val offline: Boolean
)

