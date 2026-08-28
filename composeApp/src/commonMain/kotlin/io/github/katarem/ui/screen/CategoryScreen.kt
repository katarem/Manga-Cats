package io.github.katarem.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.viewmodel.CategoryViewModel
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CategoryScreen(
    navigator: NavHostController,
    categoryTitle: String,
    tagId: String,
    viewModel: CategoryViewModel = koinViewModel(),
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData(tagId)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
            Text(
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                text = categoryTitle
            )
        }
        MangaGrid(
            modifier = Modifier.weight(9f),
            mangas = state.categoryMangas,
        ){
            val mangaJson = Json.encodeToString(it)
            navigator.navigate(Routes.MangaInfo(mangaId = it.id, mangaJson))
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearData()
        }
    }

}