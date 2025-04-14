package com.rkbapps.canvas.db

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

class DbManagerImpl: DbManager{

    private val delegate: Preferences = Preferences.userRoot().node("canvas_settings")
    private val settings = PreferencesSettings(delegate)
    private val observableSettings: ObservableSettings  = settings as ObservableSettings

    override fun getSettings(): Settings = settings

    override fun getObservableSettings(): ObservableSettings = observableSettings

}