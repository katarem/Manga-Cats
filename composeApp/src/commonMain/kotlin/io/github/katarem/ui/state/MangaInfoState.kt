package io.github.katarem.ui.state

import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga

data class MangaInfoState(
    val manga : Manga? = null,
    val chapters : List<Chapter> = emptyList(),
    val downloading: Boolean = false,
    val errorText: String = ""
)
