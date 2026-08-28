package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.viewmodel.DownloadViewModel
import io.github.katarem.ui.viewmodel.LibraryViewModel
import io.github.katarem.ui.viewmodel.RecentViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LibraryScreen(
    navigator: NavHostController,
    recentViewModel: RecentViewModel = koinViewModel<RecentViewModel>(),
    offlineViewmodel: DownloadViewModel = koinViewModel<DownloadViewModel>(),
    libraryViewModel: LibraryViewModel = koinViewModel<LibraryViewModel>()
) {

    LaunchedEffect(Unit) {
        recentViewModel.loadMangas().join()
        offlineViewmodel.getOfflineMangas().join()
    }

    val recentMangas = recentViewModel.mangas.collectAsState()
    val offlineMangas = offlineViewmodel.mangas.collectAsState()
    val filterOfflineOnly = libraryViewModel.filter.collectAsState()

    val mangas = if (filterOfflineOnly.value) offlineMangas.value.keys.toList()
    else HashSet(recentMangas.value + offlineMangas.value.keys).toList()

    Column(
        modifier = Modifier.fillMaxSize().padding(5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Show offline only")
            Checkbox(checked = filterOfflineOnly.value,
                onCheckedChange = { libraryViewModel.toggleOfflineFilter(!filterOfflineOnly.value)} )
        }
        MangaGrid(mangas = mangas) { manga ->
            navigator.navigate(Routes.Reader(manga.currentChapterIndex, manga.id, manga.offline))
        }
    }



}