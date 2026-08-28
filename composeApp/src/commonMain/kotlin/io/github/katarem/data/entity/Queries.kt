package io.github.katarem.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

data class ChapterWithPagesEntity(
    @Embedded val chapter: ChapterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "chapterId"
    )
    val pages: List<PageEntity>
)

data class MangaWithChaptersEntity(
    @Embedded val manga: MangaEntity,
    @Relation(
        entity = ChapterEntity::class,
        parentColumn = "id",
        entityColumn = "mangaId"
    )
    val chapters: List<ChapterWithPagesEntity>
)

data class MangaWithTagsEntity(
    @Embedded val manga: MangaEntity,
    @Relation(
        entity = TagEntity::class,
        parentColumn = "id",
        entityColumn = "mangaId"
    )
    val tags: List<TagEntity>
)

@Entity(
    tableName = "mangas_tags",
    primaryKeys = ["mangaId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = MangaEntity::class,
            parentColumns = ["id"],
            childColumns = ["mangaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mangaId"), Index("tagId")]
)
data class MangaTagEntity(
    val mangaId: String,
    val tagId: String
)

data class MangaWithTags(
    @Embedded val manga: MangaEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MangaTagEntity::class,
            parentColumn = "mangaId",
            entityColumn = "tagId"
        )
    )
    val tags: List<TagEntity>
)

data class TagWithMangas(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MangaTagEntity::class,
            parentColumn = "tagId",
            entityColumn = "mangaId"
        )
    )
    val mangas: List<MangaEntity>
)