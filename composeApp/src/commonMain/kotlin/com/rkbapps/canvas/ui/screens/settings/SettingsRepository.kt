package com.rkbapps.canvas.ui.screens.settings

import com.rkbapps.canvas.db.PreferenceManager
import com.rkbapps.canvas.util.AppLocaleManager

class SettingsRepository(
    private val perfManager: PreferenceManager,
    private val appLocaleManager: AppLocaleManager
) {

    val isSystemTheme = perfManager.getBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,true)
    val isDarkTheme = perfManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME,false)


    suspend fun updateIsSystemTheme(value: Boolean)=perfManager.saveBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,value)
    suspend fun updateTheme(value:Boolean) = perfManager.saveBooleanPreference(PreferenceManager.IS_DARK_THEME,value)

    fun changeLanguage(languageCode: String) {
        val language = appLocaleManager.getLanguageFromCode(languageCode)
        appLocaleManager.changeLanguage(language)
    }

    fun getLocale() = appLocaleManager.getLocale()
}
