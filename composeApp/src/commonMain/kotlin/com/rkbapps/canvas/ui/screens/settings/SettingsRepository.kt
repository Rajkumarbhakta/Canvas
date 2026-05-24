package com.rkbapps.canvas.ui.screens.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.rkbapps.canvas.db.PreferenceManager
import com.rkbapps.canvas.ui.theme.defaultColor
import com.rkbapps.canvas.util.AppLocaleManager

class SettingsRepository(
    private val perfManager: PreferenceManager,
    private val appLocaleManager: AppLocaleManager
) {

    val isSystemTheme = perfManager.getBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,true)
    val isDarkTheme = perfManager.getBooleanPreference(PreferenceManager.IS_DARK_THEME,false)
    val colorTheme = perfManager.getIntPreference(PreferenceManager.COLOR_THEME, defaultColor.toArgb())


    suspend fun updateIsSystemTheme(value: Boolean)=perfManager.saveBooleanPreference(PreferenceManager.IS_SYSTEM_THEME,value)
    suspend fun updateTheme(value:Boolean) = perfManager.saveBooleanPreference(PreferenceManager.IS_DARK_THEME,value)
    suspend fun updateColorTheme(value: Color) = perfManager.saveIntPreference(PreferenceManager.COLOR_THEME,value.toArgb())


    fun changeLanguage(languageCode: String) {
        val language = appLocaleManager.getLanguageFromCode(languageCode)
        appLocaleManager.changeLanguage(language)
    }

    fun getLocale() = appLocaleManager.getLocale()
}
