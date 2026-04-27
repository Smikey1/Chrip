package com.twugteam.admin.androidapp

import android.app.Application
import com.twugteam.admin.chirp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class ChirpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            applicationContext
            androidContext(this@ChirpApplication)
            androidLogger()
        }
    }
}