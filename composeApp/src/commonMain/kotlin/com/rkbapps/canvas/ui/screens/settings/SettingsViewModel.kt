package com.rkbapps.canvas.ui.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsViewModel(
    private val repository: SettingsRepository
): ViewModel() {

    val isSystemTheme = MutableStateFlow(false)
    val isDarkTheme = MutableStateFlow(false)
    val selectedCountry = MutableStateFlow(false)
    val isDynamicTheme = MutableStateFlow(false)


    val appVersion = "1.1.1"
}
