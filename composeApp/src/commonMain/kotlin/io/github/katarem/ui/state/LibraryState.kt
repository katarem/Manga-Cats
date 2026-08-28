package io.github.katarem.ui.state

import io.github.katarem.data.model.Manga

data class LibraryState(
    val offlineOnly: Boolean = false,
    val mangas: List<Manga> = listOf()
)
