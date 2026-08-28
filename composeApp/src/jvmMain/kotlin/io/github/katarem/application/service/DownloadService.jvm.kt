package io.github.katarem.application.service

import io.github.katarem.application.mapper.ChapterMappers
import io.github.katarem.application.mapper.MangaMappers
import io.github.katarem.data.model.Chapter
import io.github.katarem.data.model.Manga
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.util.decodeBase64Bytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import org.koin.core.parameter.emptyParametersHolder
import java.io.File
import java.nio.file.Files
import java.util.Base64

actual class DownloadService(
    private val mangaService: MangaService,
    private val dataStoreService: DataStoreService
) {

    private var _downloads: MutableStateFlow<Map<String, Float>> = MutableStateFlow(emptyMap())
    actual val downloads = _downloads.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val httpClient = HttpClient()

    val BASE_FOLDER = "${System.getProperty("user.dir")}/.mangacats/mangas"

    actual fun downloadManga(manga: Manga, chapters: List<Chapter>) {
        coroutineScope.launch {
            _downloads.value.toMutableMap().apply {
                put(manga.id, 0f)
            }

            val baseFolder = File("$BASE_FOLDER/${manga.id}")
            if(!baseFolder.exists())
                baseFolder.mkdirs()
            val coverBytes = httpClient.get(manga.coverArt).readRawBytes()
            val filename = manga.coverArt.split("/").last()
            val coverArt = "${BASE_FOLDER}/${manga.id}/$filename"
            val coverFile = File(coverArt)
            if (!coverFile.exists())
                coverFile.createNewFile()
            coverFile.writeBytes(coverBytes)
            launch {
                dataStoreService.upsertManga(
                    MangaMappers.MangaToEntity.map(manga).copy(coverArt = coverArt)
                )
            }.join()
            chapters.forEachIndexed { index, chapter ->
                val pages = mangaService.getPages(chapter.id)
                if(pages.isEmpty()) return@forEachIndexed
                val folderUrl = "${BASE_FOLDER}/${chapter.mangaId}/${chapter.id}"
                println("guardando capitulo ${chapter.chapterNumber} en $folderUrl")
                if (!File(folderUrl).exists())
                    File(folderUrl).mkdirs()

                launch { dataStoreService.upsertChapter(ChapterMappers.ChapterToEntity.map(chapter).copy(offline = true)) }.join()

                pages.forEach { page ->
                    val filename = page.split("/").last()
                    val bytes = httpClient.get(page).readRawBytes()
                    File("$folderUrl/${filename}").writeBytes(bytes)
                }
                _downloads.value.toMutableMap().apply {
                    this[manga.id] = ((index + 1f) / chapters.size) * 100f
                }
            }
            _downloads.value.toMutableMap().apply {
                remove(manga.id)
            }
        }
    }

    actual fun getChapterPages(chapter: Chapter): List<String> {
        val folderUrl =
            "$BASE_FOLDER/${chapter.mangaId}/${chapter.id}"
        return File(folderUrl).listFiles()?.map { it.absolutePath } ?: emptyList()

    }

}