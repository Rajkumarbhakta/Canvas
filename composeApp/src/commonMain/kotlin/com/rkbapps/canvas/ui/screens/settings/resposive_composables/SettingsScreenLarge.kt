package com.rkbapps.canvas.ui.screens.settings.resposive_composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.about
import canvas.composeapp.generated.resources.dark_theme
import canvas.composeapp.generated.resources.follow_system_theme
import canvas.composeapp.generated.resources.follow_system_theme_desc
import canvas.composeapp.generated.resources.personalization
import canvas.composeapp.generated.resources.privacy_policy
import canvas.composeapp.generated.resources.privacy_policy_desc
import canvas.composeapp.generated.resources.theme
import canvas.composeapp.generated.resources.theme_description
import com.rkbapps.canvas.ui.screens.settings.AppInfoHeader
import com.rkbapps.canvas.ui.screens.settings.LanguageItem
import com.rkbapps.canvas.ui.screens.settings.SettingsCategory
import com.rkbapps.canvas.ui.screens.settings.SettingsViewModel
import com.rkbapps.canvas.ui.screens.settings.TextWithArrow
import com.rkbapps.canvas.ui.screens.settings.TextWithSwitch
import org.jetbrains.compose.resources.stringResource

import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.graphics.Color
import com.rkbapps.canvas.ui.screens.settings.TextWithColor

@Composable
fun SettingsScreenLarge(
    modifier: Modifier,
    viewModel: SettingsViewModel,
    color: Color,
    isSystemTheme: Boolean,
    isDarkTheme: Boolean,
    onLanguageClick: () -> Unit,
    onThemeColorClick: () -> Unit,
) {
    var selectedCategory by remember { mutableStateOf(SettingsCategory.ABOUT) }
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = modifier
    ) {
        // Master Pane
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NavigationDrawerItem(
                label = { Text(stringResource(Res.string.about)) },
                selected = selectedCategory == SettingsCategory.ABOUT,
                onClick = { selectedCategory = SettingsCategory.ABOUT },
                icon = { Icon(Icons.Outlined.PrivacyTip, contentDescription = null) }
            )
            NavigationDrawerItem(
                label = { Text(stringResource(Res.string.personalization)) },
                selected = selectedCategory == SettingsCategory.APPEARANCE,
                onClick = { selectedCategory = SettingsCategory.APPEARANCE },
                icon = { Icon(Icons.Default.Language, contentDescription = null) }
            )
        }

        VerticalDivider()

        // Detail Pane
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 32.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (selectedCategory) {
                SettingsCategory.APPEARANCE -> {
                    item {
                        LanguageItem(
                            currentLanguageCode = viewModel.getLocale().code,
                            onClick = onLanguageClick
                        )
                    }

                    item {
                        TextWithColor(
                            text = stringResource(Res.string.theme),
                            subText = stringResource(Res.string.theme_description),
                            color = color,
                            icon = Icons.Default.Palette,
                            onClick = onThemeColorClick
                        )
                    }

                    item {
                        TextWithSwitch(
                            text = stringResource(Res.string.follow_system_theme),
                            subText = stringResource(Res.string.follow_system_theme_desc),
                            checked = isSystemTheme,
                            icon = Icons.Default.BrightnessAuto
                        ) {
                            viewModel.updateIsSystemTheme(it)
                        }
                    }
                    item {
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
                }
                SettingsCategory.ABOUT -> {
                    item {
                        AppInfoHeader(
                            appVersion = viewModel.appVersion,
                            onGithubClick = { uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas") },
                            onMailClick = { uriHandler.openUri("mailto:contact@rkbapps.in") },
                            onIssueClick = { uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas/issues") },
                            onCoffeeClick = { uriHandler.openUri("https://coff.ee/rajkumarbhakta") }
                        )
                    }
                    item {
                        TextWithArrow(
                            text = stringResource(Res.string.privacy_policy),
                            subText = stringResource(Res.string.privacy_policy_desc),
                            icon = Icons.Outlined.PrivacyTip
                        ) {
                            openUri(uriHandler,"https://sites.google.com/view/canvas-privacy/home")
                        }
                    }
                }
            }
        }
    }
}