package com.rkbapps.canvas.db

import android.content.Context
import com.rkbapps.canvas.model.DrawingState
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.flow.Flow

class DbManagerImpl(context: Context) : DbManager {
    private val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    private val setting: Settings = SharedPreferencesSettings(sharedPref)
    private val observableSettings: ObservableSettings = setting as ObservableSettings

    override fun getSettings(): Settings = setting

    override fun getObservableSettings(): ObservableSettings = observableSettings

}



