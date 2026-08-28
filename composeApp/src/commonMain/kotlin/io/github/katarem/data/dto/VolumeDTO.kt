package io.github.katarem.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class VolumeDTO(
    val volume: String,
    val chapters: Map<String, ChapterDTO>,
    val count: Int
)