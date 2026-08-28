package io.github.katarem.ui.state

import io.github.katarem.data.model.Chapter

data class ReaderState(
    val currentChapter: Chapter? = null,
    val currentChapterIndex: Int = 0,
    val pages: List<String> = emptyList(),
    val chapters: List<Chapter> = emptyList(),
    val isCascade: Boolean = false
)
