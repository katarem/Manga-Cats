package io.github.katarem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.DownloadService
import io.github.katarem.application.utils.sortByChapter
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DownloadViewModel(
    private val dataStoreService: DataStoreService,
    private val downloadService: DownloadService,
) : ViewModel() {

    private val _mangas = MutableStateFlow<Map<Manga, List<Chapter>>>(emptyMap())
    val mangas = _mangas.asStateFlow()

    val downloads = downloadService.downloads

    fun getMangas() = viewModelScope.launch {
        getOfflineMangas()
    }

    fun getOfflineMangas() = viewModelScope.launch {

        val offlineMangas = dataStoreService.getOfflineChapters().first()
            .groupBy { it.mangaId }
            .mapKeys { entry -> MangaMappers.EntityToManga.map(dataStoreService.getManga(entry.key)!!) }
            .mapValues { entry -> entry.value.map { ChapterMappers.EntityToChapter.map(it) }.sortByChapter() }

        _mangas.update { offlineMangas }

    }

}