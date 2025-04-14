package com.rkbapps.canvas.db

import com.rkbapps.canvas.model.DrawingState
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import kotlinx.coroutines.flow.Flow

class  DbManagerImpl: DbManager{

    private val settings =  StorageSettings()
    //private val observableSettings: ObservableSettings by lazy { settings as ObservableSettings}

    override fun getSettings(): Settings = settings
    override fun getObservableSettings(): ObservableSettings? = null

}