package com.rkbapps.canvas.util

import java.util.Locale

class AppLocalManagerJvm: AppLocaleManager {
    override fun changeLanguage(language: Language) {
        Locale.setDefault(Locale.forLanguageTag(language.code))
    }

    override fun getLocale(): Language {
        val currentCode = Locale.getDefault().language
        return getLanguageFromCode(currentCode)
    }

    override fun getLanguageFromCode(languageCode: String): Language {
        val code = languageCode.split("-").first()
        return appLanguages.find { it.code == code } ?: appLanguages.first()
    }
}