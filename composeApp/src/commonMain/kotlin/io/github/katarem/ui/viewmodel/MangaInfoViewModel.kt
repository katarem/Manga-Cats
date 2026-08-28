package io.github.katarem.ui.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.DownloadService
import io.github.katarem.data.model.Chapter
import io.github.katarem.application.service.MangaService
import io.github.katarem.application.utils.sortByChapter
import io.github.katarem.data.constant.Language
import io.github.katarem.data.model.Manga
import io.github.katarem.ui.state.MangaInfoState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MangaInfoViewModel(
    private val remoteService: MangaService,
    private val dataStoreService: DataStoreService,
    private val downloadService: DownloadService,
    private val preferences: DataStore<Preferences>,
) : ViewModel() {

    private var _state = MutableStateFlow(MangaInfoState())
    val state = _state.asStateFlow()

    val preferredLanguage = preferences.data.map { preferences ->
        preferences[stringPreferencesKey("preferred_language")] ?: Language.English.value
    }

    fun setManga(manga: Manga) = viewModelScope.launch {
        _state.update { it.copy(manga = manga) }
        getChapters(manga.id)
    }

    private fun getChapters(id: String) = viewModelScope.launch {
        _state.update { it.copy(errorText = "") }
        val chapterDto = remoteService.getChapters(id, preferredLanguage.first())
        _state.update {
            it.copy(
                chapters = chapterDto.filter { it.attributes.chapter != null }
                    .map(ChapterMappers.DtoToChapter::map)
                    .sortByChapter()
            )
        }
        if (_state.value.chapters.isEmpty()) {
            _state.update { it.copy(errorText = "No chapters are available for this language.") }
        }

    }

    fun setOfflineChapters(chapters: List<Chapter>) = viewModelScope.launch {
        _state.update { it.copy(chapters = chapters) }
    }

    fun downloadManga() = viewModelScope.launch {
        _state.update { it.copy(downloading = true) }
        async { downloadService.downloadManga(_state.value.manga!!,_state.value.chapters) }.join()
        _state.update { it.copy(downloading = false) }
    }

    fun saveManga(chapterIndex: Int) = viewModelScope.launch(Dispatchers.IO) {
        dataStoreService.upsertManga(MangaMappers.MangaToEntity.map(_state.value.manga!!).copy(currentChapterIndex = chapterIndex, read = true))
        dataStoreService.upsertChapter(ChapterMappers.ChapterToEntity.map(_state.value.chapters[chapterIndex]))
    }

    fun clearData() {
        _state.update { MangaInfoState() }
    }

}