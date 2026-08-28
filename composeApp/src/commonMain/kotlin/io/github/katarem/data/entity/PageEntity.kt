package io.github.katarem.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pages")
data class PageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val index: Int,
    val chapterId: String
)
