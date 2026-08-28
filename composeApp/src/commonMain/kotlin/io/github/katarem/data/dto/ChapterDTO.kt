package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDTO(
    val id: String,
    val attributes: ChapterAttributesDTO,
    val relationships: List<RelationshipDTO>
)

@Serializable
data class ChapterAttributesDTO(
    val chapter: String? = null,
    val title: String?
)
