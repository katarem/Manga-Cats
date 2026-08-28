package io.github.katarem.data.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class Manga @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val title: String = "No title",
    val coverArt: String = "",
    val language: String,
    val currentChapterIndex: Int = 0,
    val description: Map<String, String> = emptyMap(),
    val tags: List<Tag> = emptyList(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val offline: Boolean = false,
    val read: Boolean = false,
)