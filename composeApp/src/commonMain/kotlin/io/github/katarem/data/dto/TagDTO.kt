package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class TagDTO(
    val id: String,
    val attributes: TagAttributesDTO,
)

@Serializable
data class TagAttributesDTO(
    val name: Map<String, String>,
    val group: String
)