package io.github.katarem.application.service

import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.util.decodeBase64Bytes
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.*

@OptIn(BetaInteropApi::class)
actual class DownloadService(
    private val mangaService: MangaService,
    private val dataStoreService: DataStoreService
) {

    private var _downloads = MutableStateFlow<Map<String, Float>>(emptyMap())
    actual val downloads: StateFlow<Map<String, Float>> = _downloads.asStateFlow()

    private val httpClient = HttpClient()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    val fileManager = NSFileManager.defaultManager

    actual fun downloadManga(manga: Manga, chapters: List<Chapter>) {
        coroutineScope.launch {
            _downloads.value.toMutableMap().apply {
                put(manga.id, 0f)
            }
            val dir = NSSearchPathForDirectoriesInDomains(
                NSDocumentDirectory, NSUserDomainMask, true
            ).firstOrNull() as? String ?: return@launch
            val coverBytes = httpClient.get(manga.coverArt).readRawBytes()
            val filename = manga.coverArt.split("/").last()
            val coverArt = "$dir/${manga.id}/$filename"
            coverBytes.toNSData().writeToFile(coverArt, true)
            launch {
                dataStoreService.upsertManga(
                    MangaMappers.MangaToEntity.map(manga).copy(coverArt = coverArt)
                )
            }.join()
            chapters.forEachIndexed { index, chapter ->
                val folderUrl = "$dir/manga/${chapter.mangaId}/${chapter.id}"
                val pages = async { mangaService.getPages(chapter.id) }.await()
                if(pages.isEmpty()) return@forEachIndexed
                launch { dataStoreService.upsertChapter(ChapterMappers.ChapterToEntity.map(chapter)) }.join()

                pages.forEach { page ->
                    val filename = page.split("/").last()
                    val bytes = httpClient.get(page).readRawBytes()
                    bytes.toNSData().writeToFile("$folderUrl/$filename", atomically = true)
                    println("Archivo descargado: $filename, en path $folderUrl/$filename")
                }
                _downloads.value.toMutableMap().apply {
                    this[manga.id] = ((index + 1f) / chapters.size) * 100f
                }

            }
            _downloads.value.toMutableMap().apply { remove(manga.id) }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getChapterPages(chapter: Chapter): List<String> {
        val dir = NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory, NSUserDomainMask, true
        ).firstOrNull() as? String ?: return emptyList()
        val folderUrl = "$dir/manga/${chapter.mangaId}/${chapter.id}"

        val files = fileManager.contentsOfDirectoryAtPath(folderUrl, null)
            ?: return emptyList()

        return files.mapNotNull { fileName ->
            (fileName as? String)?.let { "$folderUrl/$it" }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    fun ByteArray.toNSData(): NSData =
        memScoped {
            this@toNSData.usePinned { pinned ->
                NSData.create(
                    bytes = pinned.addressOf(0),
                    length = this@toNSData.size.toULong()
                )
            }
        }

}