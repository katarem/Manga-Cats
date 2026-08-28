package io.github.katarem.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.viewmodel.RecentViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecentScreen(
    navigator: NavHostController,
    viewModel: RecentViewModel = koinViewModel<RecentViewModel>()
){

    LaunchedEffect(Unit){
        viewModel.loadMangas()
    }

    val mangas = viewModel.mangas.collectAsState()

    MangaGrid(mangas.value){ manga ->
        navigator.navigate(Routes.Reader(manga.currentChapterIndex, manga.id))
    }



}