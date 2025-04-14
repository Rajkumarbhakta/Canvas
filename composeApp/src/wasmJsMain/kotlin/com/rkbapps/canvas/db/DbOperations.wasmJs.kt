package com.rkbapps.canvas.db

import com.rkbapps.canvas.db.DbOperations.Companion.KEY
import com.russhwolf.settings.Settings

actual fun saveDrawingData(json: String,settings: Settings) = settings.putString(KEY, json)

actual fun loadDrawingData(settings: Settings): String = settings.getString(DbOperations.KEY,DbOperations.DEFAULT_VALUE)