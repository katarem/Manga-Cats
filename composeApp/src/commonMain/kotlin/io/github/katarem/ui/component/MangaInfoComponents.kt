@file:Suppress("FunctionName")

package io.github.katarem.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Tag
import kotlin.text.ifEmpty

@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int = 3
) {
    var expanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column {
        Text(
            text = text,
            maxLines = if (expanded) Int.MAX_VALUE else minimizedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { layoutResult ->
                if (!expanded) {
                    isOverflowing = layoutResult.hasVisualOverflow
                }
            },
            style = MaterialTheme.typography.bodyMedium
        )

        if (isOverflowing || expanded) {
            Text(
                text = if (expanded) "Read less" else "Read more",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}

fun LazyListScope.ChaptersColumn(
    error: String,
    chapters: List<Chapter>,
    onChapterClick: (Int, Chapter) -> Unit
) {
    if (error.isNotEmpty()) {
        item { Text(error) }
    } else if (chapters.isEmpty()) {
        item { CircularProgressIndicator() }
    } else {
        itemsIndexed(chapters) { index, chapter ->
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onChapterClick(index, chapter) },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                shape = RectangleShape
            ) {
                Text(
                    text = "${chapter.chapterNumber} - ${chapter.title.ifEmpty { "No Title" }}",
                    style = MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Start),
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    overflow = TextOverflow.Clip,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}

fun LazyListScope.MangaActionsRow(
    isDownloading: Boolean,
    onDownloadClick: () -> Unit
) = item {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton({ }) {
            Text("Add to List")
        }
        if (!isDownloading) {
            TextButton(
                onClick = onDownloadClick,
            ) {
                Text("Download")
            }
        } else {
            CircularProgressIndicator()
        }
    }
}

fun LazyListScope.DescriptionBox(
    description: String
) = item {
    Box(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        ExpandableText(
            text = description
        )
    }
}

fun LazyListScope.Title(
    title: String
) = item {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        style = MaterialTheme.typography.displayMedium
    )
}

fun LazyListScope.Cover(
    coverArt: String
) {
    item {
        AsyncImage(
            model = coverArt,
            contentDescription = coverArt,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            contentScale = ContentScale.Crop
        )
    }
}

fun LazyListScope.Tags(
    tags: List<Tag>,
    onTagClick: (Tag) -> Unit,
) {
    item {
        LazyRow(contentPadding = PaddingValues(10.dp), horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)) {
            items(tags) { item ->
                Text(
                    text = item.name,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .clickable(onClick = { onTagClick(item) }),
                )
            }
        }
    }
}