package io.github.katarem.application.service

import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

expect class DownloadService{
    val downloads: StateFlow<Map<String, Float>>
    fun downloadManga(manga: Manga, chapters: List<Chapter>)
    fun getChapterPages(chapter: Chapter): List<String>
}