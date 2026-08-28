package io.github.katarem

import io.github.katarem.data.AppDatabase

class DatabaseProvider {
    val database: AppDatabase by lazy {
        getDatabaseBuilder()
    }
}