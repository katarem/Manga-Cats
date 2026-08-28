package io.github.katarem.ui.state

import io.github.katarem.data.model.Manga
import io.github.katarem.data.model.Tag

data class MangaState(
    val categoryMangas: List<Manga> = listOf(),
    val mangas: Map<Tag, List<Manga>> = mapOf(),
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
)
