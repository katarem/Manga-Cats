package io.github.katarem.application.service

import io.github.katarem.application.utils.RateLimiter
import io.github.katarem.data.dto.ChapterDTO
import io.github.katarem.data.dto.MangaDTO
import io.github.katarem.data.dto.TagDTO
import io.github.katarem.data.dto.response.MangaPagedResponse
import io.github.katarem.data.dto.response.MangaResponse
import io.github.katarem.data.dto.response.PagesResponse
import io.github.katarem.data.model.FilterQuery
import io.github.katarem.data.model.MangaQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class MangaServiceImpl(
    private val client: HttpClient,
    private val rateLimiter: RateLimiter,
) : MangaService {

    companion object Companion {
        const val BASE_URL = "https://api.mangadex.org"
    }

    override suspend fun searchMangas(searchParams: MangaQuery): List<MangaDTO> {
        try{
            println("buscando mangas...")
            rateLimiter.acquire()
            println("rate limit me permite")
            val res = client.get("$BASE_URL/manga") {
                buildSearchUrl(searchParams)
            }
            if(res.call.response.status != HttpStatusCode.OK){
                println(res.call.response)
                return emptyList()
            }
            val body = res.body<MangaResponse<List<MangaDTO>>>()
            return body.data.filter {
                !it.attributes.lastChapter.isNullOrEmpty()
                        && !it.attributes.latestUploadedChapter.isNullOrEmpty()
            }
        } catch (e: Exception){
            println(e)
            return emptyList()
        }
    }

    override suspend fun getCover(mangaId: String,filename: String): ByteArray? {
        return try {
            rateLimiter.acquire()
            val res = client.get("$BASE_URL/manga/$filename")
            res.readRawBytes()
        } catch (ex: Exception){
            println("error cover" + ex)
            null
        }

    }

    override suspend fun searchMangasAdvanced(searchParams: MangaQuery, max: Int): Flow<List<MangaDTO>> = flow {
        try {
            var offset = 0
            var limit = 100
            var total: Int
            do {
                println("voy a buscar mangas")
                if(max in 1..offset) break
                rateLimiter.acquire()
                println("rate limit me permite")
                val apiResponse = client.get("$BASE_URL/manga") {
                    buildSearchUrl(searchParams.copy(
                        pageSize = limit,
                        offset = offset
                    ))
                }
                if(apiResponse.status != HttpStatusCode.OK){
                    println(apiResponse.call.response.bodyAsText())
                    break
                }

                val mangaResponse = apiResponse.body<MangaPagedResponse<List<MangaDTO>>>()

                emit(mangaResponse.data)

                total = mangaResponse.total
                offset += mangaResponse.data.size
                limit = if(total - offset > 100) 100 else total - offset
                delay(500)
            } while( offset < total)
        } catch (e: Exception) {
            println(e)
        }
    }

    override suspend fun getTags(): List<TagDTO> {
        return try {
            rateLimiter.acquire()
            val tags = client.get("$BASE_URL/manga/tag")
            if (tags.status != HttpStatusCode.OK) {
                println(tags.status)
                emptyList()
            }
            else tags.body<MangaResponse<List<TagDTO>>>().data
        } catch (e: Exception) {
            println("error tags: ${e.message}")
            emptyList()
        }
    }

    private fun HttpRequestBuilder.buildSearchUrl(searchParams: MangaQuery) {

        parameter("offset", searchParams.offset)
        parameter("limit", searchParams.pageSize)

        searchParams.title?.let {
            parameter("title", it)
        }

        parameter(FilterQuery.LANGUAGE.query, searchParams.language)

        searchParams.filters.forEach { (key, value) ->
            parameter(key.query, value)
        }

        parameter("includes[]","cover_art")
        parameter("hasAvailableChapters", true)
        parameter("hasUnavailableChapters", false)

    }

    override suspend fun getChapters(id: String, language: String) : List<ChapterDTO> {
        return try {
            rateLimiter.acquire()
            var response = client.get("$BASE_URL/manga/$id/feed"){
                url {
                    parameters.append("translatedLanguage[]", language)
                    parameters.append("includeExternalUrl", 0.toString())
                }
            }
            var body = response.body<MangaPagedResponse<List<ChapterDTO>>>()
            val limit = body.limit
            val total = body.total
            var offset = limit
            val mangas: MutableList<ChapterDTO> = body.data.toMutableList()

            if(mangas.isEmpty()) return emptyList()

            while(offset + limit < total){
                response = client.get("$BASE_URL/manga/$id/feed"){
                    url {
                        parameters.append("translatedLanguage[]", language)
                        parameters.append("includeExternalUrl", 0.toString())
                        parameters.append("offset", offset.toString())
                    }
                }
                body = response.body<MangaPagedResponse<List<ChapterDTO>>>()
                mangas.addAll(body.data)
                offset += limit
            }

            return mangas.toSet().toList()
        } catch (_: Exception){
            emptyList()
        }
    }

    override suspend fun getPages(chapterId: String, saver: Boolean): List<String> {
        try {
            rateLimiter.acquire()
            val response = client.get("$BASE_URL/at-home/server/$chapterId")
            val body = response.body<PagesResponse>()
            val pages = if(saver){
                body.chapter.dataSaver.map { "${body.baseUrl}/data-saver/${body.chapter.hash}/$it" }
            } else {
                body.chapter.data.map { "${body.baseUrl}/data/${body.chapter.hash}/$it" }
            }
            return pages
        } catch (ex: Exception) {
            return emptyList()
        }
    }

    override suspend fun getManga(id: String): MangaDTO? {
        return try {
            rateLimiter.acquire()
            val response = client.get("$BASE_URL/manga/$id"){
                url {
                    parameters.append("includes[]", "cover_art")
                }
            }
            response.body<MangaResponse<MangaDTO>>().data
        } catch (_: Exception){
            null
        }

    }

}