package io.github.katarem.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import io.github.katarem.data.dto.TagDTO
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.github.katarem.ui.Routes
import io.ktor.util.decodeBase64Bytes
import kotlin.collections.emptyList
import kotlin.io.encoding.Base64

@Composable
fun MangaCover(
    manga: Manga,
    open: () -> Unit,
    modifier: Modifier = Modifier.size(150.dp, 200.dp),
) {

    val imageRequest = ImageRequest.Builder(LocalPlatformContext.current)
        .data(manga.coverArt)
        .memoryCacheKey(manga.id)
        .diskCacheKey(manga.id)
        .build()

    Box(modifier) {
        Surface(onClick = open, modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageRequest,
                contentDescription = manga.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = manga.title,
                    style = MaterialTheme.typography.displaySmall,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(5.dp)
                )
            }
            if(manga.offline) {
                Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Offline",
                        style = MaterialTheme.typography.displaySmall.copy(textAlign = TextAlign.Center),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MangaGroup(title: String, mangas: List<Manga>, onMangaClick: (Manga) -> Unit, conCategoryClick: () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        textAlign = TextAlign.Left
    )
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth().padding(5.dp)) {
        items(
            items = mangas,
            key = { it.id }
            ) { item ->
            MangaCover(
                manga = item,
                open = { onMangaClick(item) },
                modifier = Modifier.size(150.dp, 200.dp).padding(2.5.dp)
            )
        }
    }
    Box(modifier = Modifier.fillMaxWidth(),contentAlignment = Alignment.CenterEnd) {
        TextButton(
            onClick = { conCategoryClick() },
        ) {
            Text(text = "View More", textAlign = TextAlign.Right)
        }
    }
}

@Composable
fun MangaGrid(
    mangas: List<Manga>,
    modifier: Modifier = Modifier,
    onMangaClick: (Manga) -> Unit,
) = LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
    ) {
        items(mangas) {
            MangaCover(
                manga = it,
                open = { onMangaClick(it) },
                modifier = Modifier.size(150.dp, 200.dp).padding(5.dp)
            )
        }
    }

@Composable
fun MangaGrid(
    downloads: Map<String, Float>,
    mangas: Map<Manga, List<Chapter>>,
    onMangaClick: (Manga, List<Chapter>) -> Unit
) = LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier.fillMaxSize()
) {
    items(mangas.entries.toList()) { (manga, chapters) ->
        if (downloads.containsKey(manga.id)) {
            DownloadingMangaCover(manga, downloads.getValue(manga.id))
        } else {
            MangaCover(
                manga = manga,
                open = { onMangaClick(manga, chapters) }
            )
        }

    }
}

@Composable
fun DownloadingMangaCover(manga: Manga, progress: Float) = Box(Modifier.size(150.dp, 200.dp)) {
    Surface(modifier = Modifier.fillMaxSize()) {
        AsyncImage(
            model = manga.coverArt,
            contentDescription = manga.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds,
            alpha = 0.3f
        )
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(5.dp)) {
            CircularProgressIndicator(
                progress = { progress / 100f },
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
    }

}