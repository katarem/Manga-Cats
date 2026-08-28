package io.github.katarem.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MangaPagedResponse<T>(
    val result: String,
    val data: T,
    val limit: Int,
    val offset: Int,
    val total: Int
)