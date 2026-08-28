package io.github.katarem.application.service

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.readRawBytes
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.Path.Companion.toPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.Base64

actual class DownloadService(
    private val mangaService: MangaService,
    private val context: Context,
    private val dataStoreService: DataStoreService
) {

    private var _downloads: MutableStateFlow<Map<String, Float>> = MutableStateFlow(emptyMap())
    actual val downloads: StateFlow<Map<String, Float>> = _downloads.asStateFlow()
    val httpClient = HttpClient()
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    val BASE_FOLDER = "${context.filesDir}/manga"

    actual fun downloadManga(manga: Manga, chapters: List<Chapter>) {
        coroutineScope.launch {
            _downloads.value = _downloads.value.toMutableMap().apply { put(manga.id, 0f) }
            val baseFolder = File("$BASE_FOLDER/${manga.id}")
            if (!baseFolder.exists())
                baseFolder.mkdirs()
            withContext(Dispatchers.IO) {
                dataStoreService.upsertManga(
                    MangaMappers.MangaToEntity.map(manga).copy(offline = true)
                )
            }
            chapters.forEachIndexed { index, chapter ->
                val pages = mangaService.getPages(chapter.id)
                if (pages.isEmpty()) return@forEachIndexed
                val folderUrl = "${BASE_FOLDER}/${chapter.mangaId}/${chapter.id}"

                if (!File(folderUrl).exists())
                    File(folderUrl).mkdirs()

                withContext(Dispatchers.IO) {
                    dataStoreService.upsertChapter(
                        ChapterMappers.ChapterToEntity.map(chapter).copy(offline = true)
                    )
                }

                pages.forEach { page ->
                    val filename = page.split("/").last()
                    val bytes = httpClient.get(page).readRawBytes()
                    File("$folderUrl/${filename}").writeBytes(bytes)
                }
                _downloads.value = _downloads.value.toMutableMap().apply {
                    this[manga.id] = ((index + 1f) / chapters.size) * 100f
                }

            }
            _downloads.value = _downloads.value.toMutableMap().apply { remove(manga.id) }

        }
    }

    actual fun getChapterPages(chapter: Chapter): List<String> {
        val folderUrl = "${context.filesDir}/manga/${chapter.mangaId}/${chapter.id}"
        return File(folderUrl).listFiles()?.map { it.absolutePath } ?: emptyList()

    }
}