package com.rkbapps.canvas.db

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings


interface DbManager{
    fun getSettings(): Settings
    fun getObservableSettings(): ObservableSettings?
}