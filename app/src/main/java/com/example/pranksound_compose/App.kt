package com.example.pranksound_compose

import android.app.Application
import com.example.pranksound_compose.utils.NetworkMonitor
import com.orhanobut.hawk.Hawk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class App : Application() {
    companion object {
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Hawk.init(this).build()
        val networkMonitor = NetworkMonitor.getInstance(this)
        networkMonitor.start()
    }
}