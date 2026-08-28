package io.github.katarem.ui.store

import io.github.katarem.data.model.Chapter

interface ChapterStore {
    fun getChapter(chapterIndex: Int): Chapter
    fun setChapters(chapters: List<Chapter>)
    fun getChapters(): List<Chapter>
}