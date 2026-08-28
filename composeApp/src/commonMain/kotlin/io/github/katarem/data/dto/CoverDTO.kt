package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoverDTO(
    val id: String,
    val attributes: CoverAttributesDTO
)

@Serializable
data class CoverAttributesDTO(
    val fileName: String,
)