package io.github.katarem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.utils.sortByChapter
import io.github.katarem.ui.state.LibraryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val dataStoreService: DataStoreService
) : ViewModel() {

    private var _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()
    fun toggleOfflineFilter() = viewModelScope.launch {
        _state.update {
            it.copy(offlineOnly = !it.offlineOnly)
        }
        loadMangas()
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