package com.example.cookbook

import android.app.Application
import com.example.cookbook.di.AppContainer
import com.example.cookbook.di.DefaultAppContainer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CookBookApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
