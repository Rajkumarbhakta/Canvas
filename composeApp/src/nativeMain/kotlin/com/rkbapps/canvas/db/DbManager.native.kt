package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.DrawingState
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import platform.Foundation.NSUserDefaults
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings

class DbManagerImpl: DbManager{

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val settings = NSUserDefaultsSettings(userDefaults)
    private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings}

    override fun getSettings(): Settings = settings

    override fun getObservableSettings(): ObservableSettings = observableSettings


}