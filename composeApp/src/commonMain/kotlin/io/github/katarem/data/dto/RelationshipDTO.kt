package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RelationshipDTO(
    val id: String,
    val type: String,
    val related: String? = null,
    val attributes: CoverAttributesDTO? = null,
)