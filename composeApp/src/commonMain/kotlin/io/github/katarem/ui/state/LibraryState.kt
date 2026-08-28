package io.github.katarem.ui.state

import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga

data class LibraryState(
    val offlineOnly: Boolean = false,
    val mangas: Map<Manga, List<Chapter>> = mapOf(),
    val downloads: Map<String, Float> = mapOf()
)
