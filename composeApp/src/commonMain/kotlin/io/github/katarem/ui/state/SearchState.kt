package io.github.katarem.ui.state

import io.github.katarem.data.constant.Demographic
import io.github.katarem.data.model.Manga
import io.github.katarem.data.model.Tag

data class SearchState(
    val showFilterDialog: Boolean = false,
    val tags: List<Tag> = emptyList(),
    val mangas: List<Manga> = emptyList(),
    val query: String = "",
    val filters: SearchFilters = SearchFilters(),
)

data class SearchFilters(
    val selectedTags: List<String> = emptyList(),
    val selectedDemographic: String = Demographic.None.value,
)
