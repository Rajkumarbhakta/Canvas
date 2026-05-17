package com.rkbapps.canvas.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.util.getAppVersion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    val isSystemTheme = repository.isSystemTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val isDarkTheme = repository.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    val appVersion = getAppVersion()

    fun updateIsSystemTheme(value: Boolean) {
        viewModelScope.launch {
            repository.updateIsSystemTheme(value)
        }
    }

    fun updateTheme(value: Boolean) {
        viewModelScope.launch {
            repository.updateTheme(value)
        }
    }

    fun changeLanguage(languageCode: String) {
        repository.changeLanguage(languageCode)
    }

    fun getLocale() = repository.getLocale()

}
