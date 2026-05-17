package com.rkbapps.canvas.db.old_db

import com.russhwolf.settings.Settings
import platform.Foundation.NSUserDefaults
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings

class DbManagerImpl: DbManager {

    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val settings = NSUserDefaultsSettings(userDefaults)
    private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings}

    override fun getSettings(): Settings = settings

    override fun getObservableSettings(): ObservableSettings = observableSettings


}