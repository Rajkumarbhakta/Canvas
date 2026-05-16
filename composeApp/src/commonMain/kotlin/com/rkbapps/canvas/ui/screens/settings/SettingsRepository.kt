package com.rkbapps.canvas.ui.screens.settings

import com.rkbapps.canvas.db.PreferenceManager

class SettingsRepository(
    private val perfManager: PreferenceManager
) {

    val isSystemTheme = perfManager.getBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,true)
    val isDarkTheme = perfManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME,false)


    suspend fun updateIsSystemTheme(value: Boolean)=perfManager.saveBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,value)
    suspend fun updateTheme(value:Boolean) = perfManager.saveBooleanPreference(PreferenceManager.IS_DARK_THEME,value)

}
