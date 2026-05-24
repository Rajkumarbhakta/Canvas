package com.rkbapps.canvas.ui.screens.settings

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkbapps.canvas.ui.theme.defaultColor
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

    val colorTheme = repository.colorTheme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        defaultColor.toArgb()
    )


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

    fun updateColorTheme(value: Color){
        viewModelScope.launch {
            repository.updateColorTheme(value)
        }
    }

    fun getLocale() = repository.getLocale()

}
