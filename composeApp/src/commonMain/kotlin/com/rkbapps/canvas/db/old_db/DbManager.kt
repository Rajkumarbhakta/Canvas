package com.rkbapps.canvas.db.old_db

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

interface DbManager{
    fun getSettings(): Settings
    fun getObservableSettings(): ObservableSettings?
}