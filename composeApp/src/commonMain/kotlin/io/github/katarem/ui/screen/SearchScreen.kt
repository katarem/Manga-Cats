package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.FilterDialog
import io.github.katarem.ui.component.MangaGrid
import io.github.katarem.ui.component.Searchbar
import io.github.katarem.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = koinViewModel<SearchViewModel>()
) {

    val state by viewModel.state.collectAsState()
    val mangas = state.mangas
    val showFilters = state.showFilterDialog

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Surface {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            var search by remember { mutableStateOf("") }
            LaunchedEffect(search) {
                delay(500)
                viewModel.search(search)
            }

            Searchbar(
                search = search,
                onTyped = { search = it },
                onSearch = { viewModel.search(search) },
                toggleFilters = { viewModel.toggleShowFilters() },
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            )
            MangaGrid(
                modifier = Modifier.fillMaxWidth(),
                mangas = mangas,
                onMangaClick = {
                    val mangaJson = Json.encodeToString(it)
                    navController.navigate(Routes.MangaInfo(it.id, mangaJson))
                }
            )
        }
        if(showFilters) {
            FilterDialog(
                onDismiss = viewModel::toggleShowFilters,
                onApply = { selectedTags, selectedDemographic ->
                    viewModel.saveFilters(selectedTags, selectedDemographic)
                    viewModel.toggleShowFilters()
                },
                tags = state.tags,
                selectedTags = state.filters.selectedTags,
                selectedDemographic = state.filters.selectedDemographic,
            )
        }
    }
}