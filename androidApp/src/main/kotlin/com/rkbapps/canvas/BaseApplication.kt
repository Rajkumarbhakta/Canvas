package com.rkbapps.canvas

import android.app.Application
import com.rkbapps.canvas.di.initKoin
import org.koin.android.ext.koin.androidContext

class BaseApplication(): Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin{
            androidContext(this@BaseApplication)
        }
    }

}