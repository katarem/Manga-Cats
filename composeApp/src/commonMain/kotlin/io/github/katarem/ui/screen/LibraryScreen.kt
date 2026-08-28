package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.viewmodel.LibraryViewModel
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    navigator: NavHostController,
    libraryViewModel: LibraryViewModel = koinViewModel<LibraryViewModel>()
) {

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        libraryViewModel.loadMangas()
    }

    val state = libraryViewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Show offline only")
            Checkbox(checked = state.value.offlineOnly,
                onCheckedChange = { libraryViewModel.toggleOfflineFilter()} )
        }
        MangaGrid(mangas = state.value.mangas) { manga ->
            scope.launch {
                libraryViewModel.setChapters(manga).join()
                navigator.navigate(Routes.Reader(manga.currentChapterIndex, manga.id, manga.offline))
            }
        }
    }



}