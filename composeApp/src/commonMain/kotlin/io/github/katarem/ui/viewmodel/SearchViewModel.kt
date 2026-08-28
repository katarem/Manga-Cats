package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.mapper.TagMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.data.model.MangaQuery
import io.github.katarem.application.service.MangaService
import io.github.katarem.data.constant.ContentRating
import io.github.katarem.data.constant.Language
import io.github.katarem.data.model.FilterQuery
import io.github.katarem.ui.state.SearchFilters
import io.github.katarem.ui.state.SearchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val service: MangaService,
    private val dataStoreService: DataStoreService,
    private val preferences: DataStore<Preferences>
) : ViewModel() {

    val preferredLanguageKey = stringPreferencesKey("preferred_language")

    val preferredRatingKey = stringPreferencesKey("preferred_rating")

    private var _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val preferredLanguage = preferences.data.map { preferences ->
        preferences[preferredLanguageKey] ?: Language.English.value
    }
    private val preferredRating = preferences.data.map { preferences ->
        preferences[preferredRatingKey] ?: ContentRating.Normal.value
    }

    fun load() = viewModelScope.launch {
        loadTags()
    }

    fun loadTags() = viewModelScope.launch(Dispatchers.IO) {
        _state.update {
            it.copy(
                tags = dataStoreService.getTags().first().map {
                    TagMappers.EntityToModel.map(it)
                }
            )
        }
    }

    fun toggleShowFilters() = viewModelScope.launch {
        _state.update {
            it.copy(
                showFilterDialog = !it.showFilterDialog,
            )
        }
    }

    fun search(query: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                mangas = emptyList()
            )
        }
        val result = service.searchMangas(MangaQuery(
            title = query,
            pageSize = 100,
            language = preferredLanguage.first(),
            filters = mapOf(
                Pair(FilterQuery.CONTENT_RATING, preferredRating.first())
            )
        ))
        val mangas = result.map { mangaDTO ->
            MangaMappers.DtoToManga.map(mangaDTO)
        }
        _state.update {
            it.copy(
                mangas = mangas
            )
        }
    }

    fun saveFilters(selectedTags: List<String>, selectedDemographic: String) = viewModelScope.launch {
        _state.update {
            it.copy(
                filters = it.filters.copy(
                    selectedTags = selectedTags,
                    selectedDemographic = selectedDemographic
                )
            )
        }
    }

}