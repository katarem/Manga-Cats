package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.application.mapper.TagMappers
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.data.model.FilterQuery
import io.github.katarem.data.model.Manga
import io.github.katarem.data.model.MangaQuery
import io.github.katarem.application.service.MangaService
import io.github.katarem.data.constant.ContentRating
import io.github.katarem.data.constant.Language
import io.github.katarem.data.dto.TagDTO
import io.github.katarem.data.entity.MangaTagEntity
import io.github.katarem.data.model.Tag
import io.github.katarem.ui.state.MangaState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.map

class HomeViewModel(
    private val remoteService: MangaService,
    private val dataStoreService: DataStoreService,
    private val preferences: DataStore<Preferences>,
) : ViewModel() {

    private var _state = MutableStateFlow(MangaState())
    val state = _state.asStateFlow()

    private val _preferredLanguage = preferences.data.map { preferences ->
        preferences[stringPreferencesKey("preferred_language")] ?: Language.English.value
    }

    private val preferredRating = preferences.data.map { preferences ->
        preferences[stringPreferencesKey("preferred_rating")] ?: ContentRating.Normal.value
    }

    fun load() = viewModelScope.launch(Dispatchers.IO) {
        dataStoreService.getMangaTags().collect { tags ->
            val tagsMangas = tags.associate {
                TagMappers.EntityToModel.map(it.tag) to
                        it.mangas.map { entity -> MangaMappers.EntityToManga.map(entity) }
            }
            _state.update { it.copy(mangas = it.mangas + tagsMangas) }
        }
    }

    fun refresh(initialLoad: Boolean = false) {
        clearData()
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = !initialLoad) }
            getMangasByTagsOnline().join()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun getMangasByTagsOnline() = viewModelScope.launch {
        val language = _preferredLanguage.first()
        val rating = preferredRating.first()

        val tags = getTags().await().map(TagMappers.DtoToModel::map)
            .sortedWith(compareBy { it.name })
        tags.filter { it.group == "genre" }.forEach { tag ->
            val query = MangaQuery(
                language = language,
                pageSize = 100,
                filters = mapOf(
                    Pair(FilterQuery.CONTENT_RATING, rating),
                    Pair(FilterQuery.INCLUDED_TAGS, tag.id)
                ),
                limit = 100
            )
            val mangas = getMangasByQuery(tag, query)
            mangas.collect { mangasWithTags ->
                val mangasWithCategory = _state.value.mangas + mangasWithTags
                _state.update { it.copy(mangas = mangasWithCategory) }
            }
            saveMangas(_state.value.mangas)
        }
    }

    private suspend fun getMangasByQuery(tag: Tag, query: MangaQuery): Flow<Map<Tag, List<Manga>>> {
        return getMangasByQuery(query).map { mangas ->
            mapOf(tag to mangas)
        }
    }

    private suspend fun saveMangas(mangasWithTags: Map<Tag, List<Manga>>) = withContext(Dispatchers.IO) {
        val entities = mangasWithTags
            .mapKeys { entry -> TagMappers.ModelToEntity.map(entry.key) }
            .mapValues { entry ->
                entry.value.map { model ->
                    MangaMappers.MangaToEntity.map(model)
                }
            }


        dataStoreService.upsertMangas(entities.values.flatten())
        dataStoreService.upsertTags(entities.keys.toList())

        entities.forEach { (tag, mangas) ->
            val mangaTags = mangas.map { MangaTagEntity(it.id, tag.id) }
            dataStoreService.upsertTagsWithMangas(mangaTags)
        }


    }


    private fun getTags(): Deferred<List<TagDTO>> = viewModelScope.async(Dispatchers.IO) {
        remoteService.getTags()
    }


    private suspend fun getMangasByQuery(query: MangaQuery): Flow<List<Manga>> {
        return remoteService.searchMangasAdvanced(query, 100).map { list ->
            list.map { MangaMappers.DtoToManga.map(it.copy(language = query.language)) }
        }
    }


    fun clearData() {
        viewModelScope.coroutineContext.cancelChildren()
        viewModelScope.launch(Dispatchers.IO) {
            launch { dataStoreService.clearAll() }.join()
            _state.update { MangaState() }
        }
    }


}