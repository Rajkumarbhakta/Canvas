package com.rkbapps.canvas.ui.screens.settings.resposive_composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.dark_theme
import canvas.composeapp.generated.resources.follow_system_theme
import canvas.composeapp.generated.resources.follow_system_theme_desc
import canvas.composeapp.generated.resources.privacy_policy
import canvas.composeapp.generated.resources.privacy_policy_desc
import canvas.composeapp.generated.resources.theme
import canvas.composeapp.generated.resources.theme_description
import com.rkbapps.canvas.ui.screens.settings.AppInfoHeader
import com.rkbapps.canvas.ui.screens.settings.LanguageItem
import com.rkbapps.canvas.ui.screens.settings.SettingsViewModel
import com.rkbapps.canvas.ui.screens.settings.TextWithArrow
import com.rkbapps.canvas.ui.screens.settings.TextWithSwitch
import org.jetbrains.compose.resources.stringResource

import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.settings.TextWithColor

@Composable
fun SettingScreenCompact(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel,
    color: Color,
    isSystemTheme: Boolean,
    isDarkTheme: Boolean,
    innerPadding: PaddingValues,
    onLanguageClick: () -> Unit,
    onThemeColorClick: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        item(key = "top header") {
            AppInfoHeader(
                appVersion = viewModel.appVersion,
                onGithubClick = { uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas") },
                onMailClick = { uriHandler.openUri("mailto:contact@rkbapps.in") },
                onIssueClick = { uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas/issues") },
                onCoffeeClick = { uriHandler.openUri("https://coff.ee/rajkumarbhakta") }
            )
        }

        item(key = "lang") {
            LanguageItem(
                currentLanguageCode = viewModel.getLocale().code,
                onClick = onLanguageClick
            )
        }

        item(key = "theme color") {
            TextWithColor(
                text = stringResource(Res.string.theme),
                subText = stringResource(Res.string.theme_description),
                icon = Icons.Default.Palette,
                color = color,
                onClick = onThemeColorClick
            )
        }

        item(key = "theme") {
            TextWithSwitch(
                text = stringResource(Res.string.follow_system_theme),
                subText = stringResource(Res.string.follow_system_theme_desc),
                checked = isSystemTheme,
                icon = Icons.Default.BrightnessAuto
            ) {
                viewModel.updateIsSystemTheme(it)
            }
        }
        item(key = "dark theme") {
            AnimatedVisibility(!isSystemTheme) {
                TextWithSwitch(
                    text = stringResource(Res.string.dark_theme),
                    checked = isDarkTheme,
                    icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode
                ) {
                    viewModel.updateTheme(it)
                }
            }
        }

        item(key = "privacy policy") {
            TextWithArrow(
                text = stringResource(Res.string.privacy_policy),
                subText = stringResource(Res.string.privacy_policy_desc),
                icon = Icons.Outlined.PrivacyTip
            ) {
                uriHandler.openUri("https://sites.google.com/view/gdealz/home")
            }
        }
    }
}