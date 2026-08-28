package io.github.katarem.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MangaResponse<T>(
    val result: String,
    val data: T
)