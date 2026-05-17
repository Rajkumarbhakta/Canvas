package com.rkbapps.canvas.db.old_db

import com.rkbapps.canvas.db.old_db.DbOperations.Companion.KEY
import com.russhwolf.settings.Settings

actual fun saveDrawingData(json: String,settings: Settings) = settings.putString(KEY, json)

actual fun loadDrawingData(settings: Settings): String = settings.getString(
    KEY,
    DbOperations.DEFAULT_VALUE)