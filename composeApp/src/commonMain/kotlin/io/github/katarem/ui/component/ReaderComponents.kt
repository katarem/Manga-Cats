package io.github.katarem.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ReaderControls(
    previousChapter: () -> Unit,
    nextChapter: () -> Unit,
    showPrevious: Boolean,
    showNext: Boolean
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showPrevious)
            ReaderButton(
                modifier = Modifier.weight(1f),
                text = "< Previous Chapter",
                onClick = previousChapter
            )
        if(showNext && showPrevious)
            Spacer(modifier = Modifier.size(5.dp))
        if (showNext)
            ReaderButton(
                modifier = Modifier.weight(1f),
                text = "Next Chapter >",
                onClick = nextChapter
            )
    }
}

@Composable
fun ReaderButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick, modifier = modifier.clip(
            RoundedCornerShape(25.dp))
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun CascadeReader(
    modifier: Modifier,
    pages: List<String>
) {
    val loadedCount = remember { mutableStateOf(0) }
    val state = rememberPagerState(pageCount = { pages.size })

    LaunchedEffect(pages){
        state.scrollToPage(0)
    }

    VerticalPager(
        modifier = modifier,
        state = state
    ) { index ->
        var loaded by remember { mutableStateOf(false) }
        val zoomState = rememberZoomState()
        Surface {
            if (!loaded) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ){
                    CircularProgressIndicator()
                }
            }
            AsyncImage(
                model = pages[index],
                contentDescription = null,
                modifier = Modifier.fillMaxSize().zoomable(zoomState),
                onSuccess = { state ->
                    if (loadedCount.value == index) {
                        loadedCount.value += 1
                    }
                    loaded = true
                },

                )
        }
    }
}

@Composable
fun BookReader(
    modifier: Modifier,
    pages: List<String>
) {
    val loadedCount = remember { mutableStateOf(0) }
    val state = rememberPagerState(pageCount = { pages.size })
    val zoomState = rememberZoomState()

    LaunchedEffect(pages) {
        state.scrollToPage(0)
    }

    HorizontalPager(
        state = state,
        modifier = modifier
    ) { index ->
        var loading by remember { mutableStateOf(false) }
        if (loading) {
            CircularProgressIndicator()
        }

        AsyncImage(
            model = pages[index],
            contentDescription = null,
            onSuccess = {
                if (loadedCount.value == index) {
                    loadedCount.value += 1
                }
                loading = false
            },
            modifier = Modifier.fillMaxSize().zoomable(zoomState)
        )
    }
}

@Composable
fun ReaderComponent(
    modifier: Modifier,
    pages: List<String>,
    isCascade: Boolean
) {

    if (isCascade) {
        CascadeReader(
            modifier = modifier,
            pages = pages
        )
    } else {
        BookReader(
            modifier = modifier,
            pages = pages
        )
    }
}