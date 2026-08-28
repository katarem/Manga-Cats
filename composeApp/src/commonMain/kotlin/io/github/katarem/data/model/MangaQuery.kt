package io.github.katarem.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MangaQuery(
    val title: String? = null,
    val language: String = "en",
    val filters: Map<FilterQuery, String> = emptyMap(),
    val pageSize: Int? = null,
    val offset: Int = 0,
    val limit: Int? = null,
)

enum class FilterQuery(val query: String) {
    LANGUAGE("availableTranslatedLanguage[]"),
    MOST_POPULAR("order[rating]"),
    DEMOGRAPHIC("publicationDemographic[]"),
    STATUS("status[]"),
    INCLUDED_TAGS("includedTags[]"),
    CONTENT_RATING("contentRating[]")
}