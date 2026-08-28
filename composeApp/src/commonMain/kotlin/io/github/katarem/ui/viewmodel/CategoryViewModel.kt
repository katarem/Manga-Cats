package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.service.MangaService
import io.github.katarem.data.constant.Language
import io.github.katarem.data.model.FilterQuery
import io.github.katarem.data.model.MangaQuery
import io.github.katarem.ui.state.MangaState
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val remoteService: MangaService,
    private val preferences: DataStore<Preferences>
) : ViewModel() {

    private var _state = MutableStateFlow(MangaState())
    val state = _state.asStateFlow()

    private var loader: Job? = null
    private val preferredLanguage = preferences.data.map { preferences ->
        preferences[stringPreferencesKey("preferred_language")] ?: Language.English.value
    }

    fun clearData() = viewModelScope.launch {
        loader?.cancelAndJoin()
        _state.update { MangaState() }
    }

    fun loadData(tagId: String) = viewModelScope.launch {
        loader?.cancelAndJoin()
        loader = loadCategory(tagId)
    }

    fun loadCategory(tagId: String): Job = viewModelScope.launch {
        val query = MangaQuery(
            filters = mapOf(
                FilterQuery.INCLUDED_TAGS to tagId
            ),
            language = preferredLanguage.first(),
            pageSize = 100
        )
        remoteService.searchMangasAdvanced(query).collect { loaded ->
            val mangas = loaded.map { MangaMappers.DtoToManga.map(it) }
            _state.update { it.copy(categoryMangas = it.categoryMangas + mangas) }
        }
    }

}