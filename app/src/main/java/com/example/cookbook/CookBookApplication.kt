package com.example.cookbook

import android.app.Application
import com.example.cookbook.di.AppContainer
import com.example.cookbook.di.DefaultAppContainer

/**
 * Clase Application personalizada para la aplicación CookBook.
 * Mantiene la instancia única del AppContainer para proveer dependencias
 * de manera manual y centralizada a lo largo de toda la app.
 */
class CookBookApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(applicationContext)
    }
}
