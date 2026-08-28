package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.MangaService
import io.github.katarem.application.utils.sortByChapter
import io.github.katarem.data.constant.Language
import io.github.katarem.data.model.Manga
import io.github.katarem.ui.state.LibraryState
import io.github.katarem.ui.store.ChapterStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val dataStoreService: DataStoreService,
    private val chapterStore: ChapterStore,
    private val remoteService: MangaService,
    private val preferences: DataStore<Preferences>,
) : ViewModel() {

    private var _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()
    fun toggleOfflineFilter() = viewModelScope.launch {
        _state.update {
            it.copy(offlineOnly = !it.offlineOnly)
        }
        loadMangas()
    }

    val preferredLanguage = preferences.data.map { preferences ->
        preferences[stringPreferencesKey("preferred_language")] ?: Language.English.value
    }

    fun setChapters(manga: Manga) = viewModelScope.launch {
        val chapters = if(manga.offline) dataStoreService.getChapterByManga(manga.id)
            .map(ChapterMappers.EntityToChapter::map)
        else remoteService.getChapters(manga.id, preferredLanguage.first())
            .map(ChapterMappers.DtoToChapter::map)
        chapterStore.setChapters(chapters.sortByChapter())
    }


    fun loadMangas() = viewModelScope.launch {
        val mangas = if (_state.value.offlineOnly) {
            dataStoreService.getOfflineChapters().first()
                .groupBy { it.mangaId }
                .mapKeys { entry -> MangaMappers.EntityToManga.map(dataStoreService.getManga(entry.key)!!) }
                .mapValues { entry ->
                    entry.value.map { ChapterMappers.EntityToChapter.map(it) }.sortByChapter()
                }.keys.toList()
        } else {
            dataStoreService.getAllMangas().first()
                .map(MangaMappers.EntityToManga::map)
                .filter { it.offline || it.read }
        }
        _state.update { it.copy(mangas = mangas) }
    }


}