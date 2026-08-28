package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PagesDTO(
    val data: List<String>,
    val dataSaver: List<String>,
    val hash: String
)