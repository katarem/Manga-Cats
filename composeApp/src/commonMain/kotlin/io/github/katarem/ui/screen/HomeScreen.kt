package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.katarem.data.model.Manga
import io.github.katarem.data.model.Tag
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.MangaGroup
import io.github.katarem.ui.state.MangaState
import io.github.katarem.ui.viewmodel.HomeViewModel
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigator: NavHostController,
    viewModel: HomeViewModel = koinViewModel<HomeViewModel>()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    if(state.mangas.isEmpty() && state.isRefreshing) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        MangasByTags(
            state = state,
            onRefresh = { viewModel.refresh() },
            onMangaClick = { manga ->
                navigator.navigate(Routes.MangaInfo(mangaId = "", mangaJson = Json.encodeToString(manga)))
            },
            onCategoryClick = { tag ->
                navigator.navigate(Routes.Category(name = tag.name, tagId = tag.id))
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangasByTags(
    state: MangaState,
    onRefresh: () -> Unit,
    onMangaClick: (Manga) -> Unit,
    onCategoryClick: (Tag) -> Unit,
) = PullToRefreshBox(
    isRefreshing = state.isRefreshing,
    onRefresh = onRefresh
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            items = state.mangas.toList(),
            key = { it.first.id }
        ) { (tag, mangas) ->
            MangaGroup(
                title = tag.name,
                mangas = mangas,
                onMangaClick = onMangaClick,
                conCategoryClick = { onCategoryClick(tag) }
            )
        }
    }
}
