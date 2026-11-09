package com.example.cookbook

import android.app.Application
import com.example.cookbook.di.AppContainer
import com.example.cookbook.di.DefaultAppContainer

class CookBookApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
