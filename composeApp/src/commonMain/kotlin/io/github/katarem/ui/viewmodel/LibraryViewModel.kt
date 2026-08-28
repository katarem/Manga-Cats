package io.github.katarem.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.MangaService
import io.github.katarem.ui.state.LibraryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(
) : ViewModel() {

    private var state = LibraryState()
    private var _filter = MutableStateFlow(state.offlineOnly)
    val filter = _filter.asStateFlow()

    fun toggleOfflineFilter(newState: Boolean) = viewModelScope.launch {
        _filter.update { newState }
    }



}