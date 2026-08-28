package io.github.katarem.data.dto.response

import io.github.katarem.data.dto.PagesDTO
import kotlinx.serialization.Serializable

@Serializable
data class PagesResponse(
    val result: String,
    val baseUrl: String,
    val chapter: PagesDTO
)