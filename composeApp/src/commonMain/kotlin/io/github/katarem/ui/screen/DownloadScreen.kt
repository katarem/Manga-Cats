package io.github.katarem.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.viewmodel.DownloadViewModel
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadScreen(
    navigator: NavController,
    mangaInfoViewModel: MangaInfoViewModel,
    viewModel: DownloadViewModel = koinViewModel<DownloadViewModel>()
){

    val downloads by viewModel.downloads.collectAsState()

    LaunchedEffect(downloads.size){
        viewModel.getMangas()
    }

    val mangas by viewModel.mangas.collectAsState()

    MangaGrid(mangas = mangas, downloads = downloads){ manga, chapters ->
        mangaInfoViewModel.setOfflineChapters(chapters)
        navigator.navigate(Routes.Reader(manga.currentChapterIndex,manga.id,true))
    }
}