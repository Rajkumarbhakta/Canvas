package com.rkbapps.canvas.util

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class AppLocalManagerAndroid(private val context: Context): AppLocaleManager {
    override fun changeLanguage(language: Language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(language.code)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
        }
    }

    override fun getLocale(): Language {
        val code = try {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.getSystemService(LocaleManager::class.java)
                    ?.applicationLocales
                    ?.get(0)
            } else {
                AppCompatDelegate.getApplicationLocales().get(0)
            }
            locale?.language ?: getDefaultLanguageCode()
        }catch (e: Exception){
            getDefaultLanguageCode()
        }
        return getLanguageFromCode(code)
    }

    override fun getLanguageFromCode(languageCode: String): Language {
        return appLanguages.find { it.code == languageCode } ?: appLanguages.first()
    }
    private fun getDefaultLanguageCode(): String {
        return  appLanguages.first().code
    }
}