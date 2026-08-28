package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.DownloadService
import io.github.katarem.data.model.Chapter
import io.github.katarem.application.service.MangaService
import io.github.katarem.application.utils.sortByChapter
import io.github.katarem.ui.state.ReaderState
import io.github.katarem.ui.store.ChapterStore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ReaderViewModel(
    private val service: MangaService,
    private val dataService: DataStoreService,
    private val downloadService: DownloadService,
    private val offlineChapterStore: ChapterStore,
    preferences: DataStore<Preferences>
) : ViewModel() {

    private var _state = MutableStateFlow(ReaderState())
    val state = _state.asStateFlow()

    val isCascade = preferences.data
        .map { it[booleanPreferencesKey("preferred_reading_mode")] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    private val savingMode = preferences.data
        .map { it[booleanPreferencesKey("preferred_quality")] ?: false }

    private fun getPages(chapterId: String, saver: Boolean) = viewModelScope.async {
        service.getPages(chapterId, saver)
    }

    private fun getChapters(mangaId: String) : Deferred<List<Chapter>> = viewModelScope.async {
        return@async service.getChapters(mangaId).map(ChapterMappers.DtoToChapter::map).sortByChapter()
    }

    fun loadMangaOnline(mangaId: String, chapterIndex: Int) = viewModelScope.launch{
        val chapters = offlineChapterStore.getChapters()
        val chaptersToRead = chapters.ifEmpty { getChapters(mangaId).await() }
        val pages = getPages(chaptersToRead[chapterIndex].id, savingMode.first()).await()
        val currentChapter = chaptersToRead[chapterIndex]
        _state.update { it.copy(pages = pages, currentChapterIndex = chapterIndex, chapters = chaptersToRead, currentChapter = currentChapter) }
    }

    fun loadMangaOffline(chapterIndex: Int) = viewModelScope.launch{
        val chapters = offlineChapterStore.getChapters()
        val chapter = chapters[chapterIndex]
        val pages = downloadService.getChapterPages(chapter)
        _state.update { it.copy(pages = pages, currentChapterIndex = chapterIndex, chapters = chapters, currentChapter = chapter) }
    }

    fun loadManga(mangaId: String, chapterIndex: Int, offline: Boolean) = viewModelScope.launch {
        if(offline){
            loadMangaOffline(chapterIndex)
        } else {
            loadMangaOnline(mangaId, chapterIndex)
        }
    }

    fun changeChapter(newIndex: Int) = viewModelScope.launch {
        val chapter = _state.value.chapters[newIndex]
        if(chapter.offline){
            changeChapterOffline(chapter, newIndex)
        } else {
            changeChapterOnline(chapter, newIndex)
        }
    }

    private fun changeChapterOffline(newChapter: Chapter, newIndex: Int) = viewModelScope.launch {
        val pages = downloadService.getChapterPages(newChapter)
        _state.update { it.copy(pages = pages, currentChapterIndex = newIndex, currentChapter = newChapter) }
    }

    private fun changeChapterOnline(newChapter: Chapter, newIndex: Int) = viewModelScope.launch {
        val pages = getPages(newChapter.id, savingMode.first()).await()
        _state.update { it.copy(pages = pages, currentChapterIndex = newIndex, currentChapter = newChapter) }
    }

    fun saveChapter() = viewModelScope.launch {
        val chapterEntity = ChapterMappers.ChapterToEntity.map(_state.value.currentChapter!!)
        dataService.getManga(_state.value.currentChapter!!.mangaId)?.let { manga ->
            dataService.upsertManga(manga.copy(currentChapterIndex = _state.value.currentChapterIndex, updatedAt = Clock.System.now().toEpochMilliseconds() ))
        }
        dataService.upsertChapter(chapterEntity.copy(mangaId = _state.value.currentChapter!!.mangaId))
    }

}