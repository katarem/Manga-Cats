
package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class MangaDTO(
    val id: String,
    val type: String,
    val attributes: MangaAttributesDTO,
    val relationships: List<RelationshipDTO>,
    val language: String = "",
)

@Serializable
data class MangaAttributesDTO(
    val title: Map<String, String>,
    val altTitles: List<Map<String, String>>,
    val description: Map<String, String>,
    val originalLanguage: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String,
    val officialLinks: Map<String, String>? = null,
    val lastChapter: String?,
    val latestUploadedChapter: String?,
    val availableTranslatedLanguages: List<String?>,
    val tags: List<TagDTO>,
)

