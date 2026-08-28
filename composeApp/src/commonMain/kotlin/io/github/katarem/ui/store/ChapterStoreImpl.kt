package io.github.katarem.ui.store

import io.github.katarem.data.model.Chapter

class ChapterStoreImpl : ChapterStore {

    private var _chapters = listOf<Chapter>()

    override fun setChapters(chapters: List<Chapter>) {
        _chapters = chapters
    }

    override fun getChapters(): List<Chapter> {
        return _chapters
    }

    override fun getChapter(chapterIndex: Int): Chapter {
        return _chapters[chapterIndex]
    }

}