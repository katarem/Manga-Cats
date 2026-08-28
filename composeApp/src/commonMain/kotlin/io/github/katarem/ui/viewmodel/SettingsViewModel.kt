package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.data.constant.ContentRating
import io.github.katarem.data.constant.Language
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferences: DataStore<Preferences>,
): ViewModel() {

    val preferredReadingMode = preferences.data
        .map { preferences -> preferences[booleanPreferencesKey("preferred_reading_mode")] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)
    val preferredLanguage = preferences.data
        .map { preferences -> preferences[stringPreferencesKey("preferred_language")] ?: Language.English.value }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), Language.English.value)
    val preferredQuality = preferences.data
        .map { preferences -> preferences[booleanPreferencesKey("preferred_quality")] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val preferredRating = preferences.data
        .map { preferences -> preferences[stringPreferencesKey("preferred_rating")] ?: ContentRating.Normal.value }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), ContentRating.Normal.value)

    fun setPreferredLanguage(preferredLanguage: String) = viewModelScope.launch {
        preferences.updateData {
            it.toMutablePreferences().apply {
                this[stringPreferencesKey("preferred_language")] = preferredLanguage
            }
        }
    }
    fun setPreferredReadingMode(preferredReadingMode: Boolean) = viewModelScope.launch {
        preferences.updateData {
            it.toMutablePreferences().apply {
                this[booleanPreferencesKey("preferred_reading_mode")] = preferredReadingMode
            }
        }
    }
    fun setPreferredQualityMode(preferredQualityMode: Boolean) = viewModelScope.launch {
        preferences.updateData {
            it.toMutablePreferences().apply {
                this[booleanPreferencesKey("preferred_quality")] = preferredQualityMode
            }
        }
    }

    fun setPreferredRating(rating: String) = viewModelScope.launch {
        preferences.updateData {
            it.toMutablePreferences().apply {
                this[stringPreferencesKey("preferred_rating")] = rating
            }
        }
    }
}