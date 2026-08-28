package io.github.katarem.di

import coil3.ImageLoader
import io.github.katarem.application.repository.ChapterRepository
import io.github.katarem.application.repository.ChapterRepositoryImpl
import io.github.katarem.application.repository.MangaRepository
import io.github.katarem.application.repository.MangaRepositoryImpl
import io.github.katarem.application.repository.TagRepository
import io.github.katarem.application.repository.TagRepositoryImpl
import io.github.katarem.application.service.DataStoreService
import io.github.katarem.application.service.DataStoreServiceImpl
import io.github.katarem.application.service.MangaService
import io.github.katarem.application.service.MangaServiceImpl
import io.github.katarem.application.utils.RateLimiter
import io.github.katarem.ui.store.ChapterStore
import io.github.katarem.ui.store.ChapterStoreImpl
import io.github.katarem.ui.viewmodel.CategoryViewModel
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import io.github.katarem.ui.viewmodel.HomeViewModel
import io.github.katarem.ui.viewmodel.LibraryViewModel
import io.github.katarem.ui.viewmodel.ReaderViewModel
import io.github.katarem.ui.viewmodel.SearchViewModel
import io.github.katarem.ui.viewmodel.SettingsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedModule = module {
    viewModelOf(::MangaInfoViewModel)
    viewModelOf(::ReaderViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::CategoryViewModel)
    viewModelOf(::LibraryViewModel)
}

val dataModule = module {
    single {
        MangaServiceImpl(get(), get())
    }.bind<MangaService>()

    single {
        DataStoreServiceImpl(get(),get(), get())
    }.bind<DataStoreService>()

    single {
        MangaRepositoryImpl(get())
    }.bind<MangaRepository>()

    single {
        ChapterRepositoryImpl(get())
    }.bind<ChapterRepository>()

    single {
        TagRepositoryImpl(get())
    }.bind<TagRepository>()

    single {
        ImageLoader(get())
    }

    single {
        ChapterStoreImpl()
    }.bind<ChapterStore>()

    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    single {
        RateLimiter(5, get())
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }.bind<HttpClient>()

}

val platformModule = platformModule()
