package com.rkbapps.canvas.db.old_db

import android.content.Context
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

class DbManagerImpl(context: Context) : DbManager {
    private val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    private val setting: Settings = SharedPreferencesSettings(sharedPref)
    private val observableSettings: ObservableSettings = setting as ObservableSettings

    override fun getSettings(): Settings = setting

    override fun getObservableSettings(): ObservableSettings = observableSettings

}



