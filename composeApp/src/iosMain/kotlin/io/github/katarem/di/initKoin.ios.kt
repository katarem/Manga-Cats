package io.github.katarem.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import io.github.katarem.application.service.DownloadService
import io.github.katarem.createDataStore
import io.github.katarem.data.AppDatabase
import io.github.katarem.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<AppDatabase> { getDatabaseBuilder() }
    single<DownloadService> { DownloadService(get(),get()) }
    single<DataStore<Preferences>> { createDataStore() }

}