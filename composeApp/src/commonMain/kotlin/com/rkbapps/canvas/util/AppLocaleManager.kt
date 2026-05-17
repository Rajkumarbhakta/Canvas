package com.rkbapps.canvas.util

data class Language(
    val name: String,
    val code: String,
    val displayLanguage: String
)

val appLanguages = listOf(
    Language("english","en", "English"), // default language
    Language("russian","ru", "Русский"),
    Language("portuguese", "pt", "Português"),
    Language("spanish", "es", "Español"),
    Language("german", "de", "Deutsch"),
    Language("french", "fr", "Français"),
    Language("japanese", "ja", "日本語"),
    Language("korean", "ko", "한국어"),
    Language("hindi","hi", "हिन्दी"),
    Language("bengali", "bn", "বাংলা"),
    )


interface AppLocaleManager {
    fun changeLanguage(language: Language)
    fun getLocale(): Language
    fun getLanguageFromCode(languageCode: String): Language
}
