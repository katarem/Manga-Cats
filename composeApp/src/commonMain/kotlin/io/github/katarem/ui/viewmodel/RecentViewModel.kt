package io.github.katarem.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.data.model.Manga
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecentViewModel(
    private val dataStoreService: DataStoreService) : ViewModel() {

    private var _mangas = MutableStateFlow<List<Manga>>(listOf())
    val mangas = _mangas.asStateFlow()

    fun loadMangas() = viewModelScope.launch {
        _mangas.update {
            dataStoreService.getAllMangas().first()
                .map(MangaMappers.EntityToManga::map)
                .filter { !it.offline && it.read }
        }
    }


}
