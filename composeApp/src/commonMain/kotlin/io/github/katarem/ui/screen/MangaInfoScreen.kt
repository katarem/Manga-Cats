package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import io.github.katarem.data.constant.Language
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.github.katarem.ui.Routes
import io.github.katarem.ui.component.ChaptersColumn
import io.github.katarem.ui.component.Cover
import io.github.katarem.ui.component.DescriptionBox
import io.github.katarem.ui.component.MangaActionsRow
import io.github.katarem.ui.component.Tags
import io.github.katarem.ui.component.Title
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MangaInfoScreen(
    navigator: NavHostController,
    mangaJson: String,
    viewModel: MangaInfoViewModel = koinViewModel<MangaInfoViewModel>()
) {

    val state by viewModel.state.collectAsState()
    val manga = state.manga
    val chapters = state.chapters
    val error = state.errorText
    val isDownloading = state.downloading

    val preferredLanguage by viewModel.preferredLanguage.collectAsState(initial = null)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearData()
        }
    }

    LaunchedEffect(Unit) {
        val manga = Json.decodeFromString<Manga>(mangaJson)
        viewModel.setManga(manga)
    }

    val onChapterClick: (Int, Chapter) -> Unit = { index, chapter ->
        viewModel.saveManga(index)
        navigator.navigate(
            Routes.Reader(
                index,
                chapter.mangaId
            )
        )
    }

    manga?.let {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            Cover(it.coverArt)
            Title(it.title)
            Tags(it.tags) { tag ->
                navigator.navigate(Routes.Category(tag.name, tag.id))
            }
            preferredLanguage?.let { lang ->
                DescriptionBox(it.description[lang] ?: it.description[Language.English.value] ?: "")
            }
            MangaActionsRow(
                isDownloading = isDownloading,
                onDownloadClick = viewModel::downloadManga
            )
            ChaptersColumn(error, chapters, onChapterClick)
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }

}

