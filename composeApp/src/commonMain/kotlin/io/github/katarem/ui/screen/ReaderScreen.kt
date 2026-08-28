package io.github.katarem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.katarem.di.sharedModule
import io.github.katarem.ui.component.ReaderComponent
import io.github.katarem.ui.component.ReaderControls
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import io.github.katarem.ui.viewmodel.ReaderViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReaderScreen(
    mangaId: String,
    startingChapterIndex: Int,
    offline: Boolean,
    viewModel: ReaderViewModel = koinViewModel<ReaderViewModel>()
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val isCascade by viewModel.isCascade.collectAsStateWithLifecycle()
    val currentChapterIndex = state.currentChapterIndex

    LaunchedEffect(Unit){
        viewModel.loadManga(mangaId,startingChapterIndex, offline).join()
    }

    LaunchedEffect(viewModel.state.value.currentChapter) {
        viewModel.state.value.currentChapter?.let {
            viewModel.saveChapter()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(vertical = 10.dp)) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            val title = state.currentChapter?.let { chapter ->
                "${chapter.chapterNumber} ${chapter.title.ifEmpty { "No Title" }}"
            } ?: ""
                Text(
                modifier = Modifier,
                text = title,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
        ReaderComponent(
            modifier = Modifier.fillMaxWidth().weight(9f),
            pages = state.pages,
            isCascade = isCascade
        )
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.75f).padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            ReaderControls(
                showPrevious = currentChapterIndex > 0,
                showNext = currentChapterIndex < state.chapters.size - 1,
                previousChapter = {
                    val newIndex = state.currentChapterIndex - 1
                    viewModel.changeChapter(newIndex)
                },
                nextChapter = {
                    val newIndex = state.currentChapterIndex + 1
                    viewModel.changeChapter(newIndex)
                }
            )
        }
    }


}
