package io.github.katarem

import android.app.Application
import io.github.katarem.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin{
            DatabaseProvider(this@MyApplication).database
            androidContext(this@MyApplication)
        }
    }
}