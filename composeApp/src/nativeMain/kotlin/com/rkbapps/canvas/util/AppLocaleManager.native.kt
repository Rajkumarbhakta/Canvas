package com.rkbapps.canvas.util

import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

class AppLocalManagerNative: AppLocaleManager {
    override fun changeLanguage(language: Language) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(language.code), "AppleLanguages")
        NSUserDefaults.standardUserDefaults.synchronize()
    }

    override fun getLocale(): Language {
        val currentCode = (NSUserDefaults.standardUserDefaults.stringArrayForKey("AppleLanguages")?.firstOrNull() as? String)
            ?: NSLocale.currentLocale.languageCode
        return getLanguageFromCode(currentCode)
    }

    override fun getLanguageFromCode(languageCode: String): Language {
        val code = languageCode.split("-").first()
        return appLanguages.find { it.code == code } ?: appLanguages.first()
    }
}