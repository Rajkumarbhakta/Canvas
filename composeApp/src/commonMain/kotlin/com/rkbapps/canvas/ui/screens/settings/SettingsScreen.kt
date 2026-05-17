package com.rkbapps.canvas.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.app_name
import canvas.composeapp.generated.resources.bengali
import canvas.composeapp.generated.resources.buy_me_a_coffee
import canvas.composeapp.generated.resources.confirm
import canvas.composeapp.generated.resources.dark_theme
import canvas.composeapp.generated.resources.english
import canvas.composeapp.generated.resources.follow_system_theme
import canvas.composeapp.generated.resources.follow_system_theme_desc
import canvas.composeapp.generated.resources.french
import canvas.composeapp.generated.resources.german
import canvas.composeapp.generated.resources.github
import canvas.composeapp.generated.resources.hindi
import canvas.composeapp.generated.resources.japanese
import canvas.composeapp.generated.resources.korean
import canvas.composeapp.generated.resources.language
import canvas.composeapp.generated.resources.language_description
import canvas.composeapp.generated.resources.mail
import canvas.composeapp.generated.resources.portuguese
import canvas.composeapp.generated.resources.privacy_policy
import canvas.composeapp.generated.resources.privacy_policy_desc
import canvas.composeapp.generated.resources.raise_a_issue
import canvas.composeapp.generated.resources.russian
import canvas.composeapp.generated.resources.search_here
import canvas.composeapp.generated.resources.select_language
import canvas.composeapp.generated.resources.spanish
import canvas.composeapp.generated.resources.settings
import com.rkbapps.canvas.util.appLanguages
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uriHandler = LocalUriHandler.current

    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var isLanguageDialogOpen by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text(stringResource(Res.string.settings))},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }){
                        Icon(Icons.AutoMirrored.Default.ArrowBack,"")
                    }
                }
            )
        }
    ) { innerPadding ->

        if (isLanguageDialogOpen) {
            Dialog(
                onDismissRequest = { isLanguageDialogOpen = false }
            ) {
                LanguageSelectionDialog(
                    modifier = Modifier.height(500.dp),
                    currentLanguageCode = viewModel.getLocale().code
                ) { languageCode ->
                    viewModel.changeLanguage(languageCode)
                    isLanguageDialogOpen = false
                }
            }
        }


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            item(key="top header") {
                ElevatedCard (
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                    ) {

                        Text(stringResource(Res.string.app_name),
                            style = MaterialTheme.typography.headlineLarge)
                        Text("v${viewModel.appVersion}")

                        Row(modifier = Modifier.padding(vertical = 10.dp)) {
                            FilledIconButton(
                                onClick={ uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas") },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.github),
                                    contentDescription = stringResource(Res.string.github),
                                    modifier = Modifier.padding(2.dp)
                                )
                            }
                            FilledIconButton(
                                onClick = { uriHandler.openUri("mailto:contact@rkbapps.in") },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.onPrimary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Mail, contentDescription = stringResource(Res.string.mail))
                            }

                        }

                        HorizontalDivider()

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = {
                                uriHandler.openUri("https://github.com/Rajkumarbhakta/Canvas/issues")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.BugReport,"")
                                Text(stringResource(Res.string.raise_a_issue))
                            }
                        }

                        Button(
                            onClick = {
                                uriHandler.openUri("https://coff.ee/rajkumarbhakta")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Row (verticalAlignment = Alignment.CenterVertically){
                                Icon(imageVector = Icons.Default.Coffee,"")
                                Text(stringResource(Res.string.buy_me_a_coffee))
                            }
                        }

                    }
                }
            }

            item(key="lang") {
                LanguageItem(
                    currentLanguageCode = viewModel.getLocale().code
                ) {
                    isLanguageDialogOpen = true
                }
            }

            item(key="theme") {
                TextWithSwitch(
                    text = stringResource(Res.string.follow_system_theme),
                    subText = stringResource(Res.string.follow_system_theme_desc),
                    checked = isSystemTheme,
                    icon = Icons.Default.BrightnessAuto
                ) {
                    viewModel.updateIsSystemTheme(it)
                }
            }
            item(key="dark theme") {
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

            item(key="privacy policy") {
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


}

//


@Composable
fun TextWithSwitch(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    subText:String? = null,
    checked: Boolean = false,
    onChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
        )
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text,

                style = MaterialTheme.typography.titleLarge,
            )
            subText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}


@Composable
fun TextWithArrow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    text: String,
    subText:String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
        )
        Column(modifier = Modifier.weight(1f),) {
            Text(
                text,

                style = MaterialTheme.typography.titleLarge,
            )
            subText?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        IconButton(
            onClick = onClick
        ) {
            Icon(Icons.AutoMirrored.Default.ArrowForward,"")
        }
    }
}


@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    currentLanguageCode: String,
    onClick: () -> Unit
) {
    val languageName = when (currentLanguageCode) {
        "en" -> stringResource(Res.string.english)
        "ru" -> stringResource(Res.string.russian)
        "pt" -> stringResource(Res.string.portuguese)
        "es" -> stringResource(Res.string.spanish)
        "de" -> stringResource(Res.string.german)
        "fr" -> stringResource(Res.string.french)
        "ja" -> stringResource(Res.string.japanese)
        "ko" -> stringResource(Res.string.korean)
        "hi" -> stringResource(Res.string.hindi)
        "bn" -> stringResource(Res.string.bengali)
        else -> stringResource(Res.string.english)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = stringResource(Res.string.language),
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 8.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                stringResource(Res.string.language),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                stringResource(Res.string.language_description),
                style = MaterialTheme.typography.labelSmall
            )

        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    onClick()
                }
        ) {
            Text(
                "$languageName \uD83D\uDD3D",
                modifier = Modifier.padding(8.dp)
            )
        }

    }
}

@Composable
fun LanguageSelectionDialog(
    modifier: Modifier = Modifier,
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit
) {
    var selectedLanguageCode by remember { mutableStateOf(currentLanguageCode) }
    var searchQuery by remember { mutableStateOf("") }
    var languageList by remember { mutableStateOf(appLanguages) }

    LaunchedEffect(searchQuery) {
        languageList = if (searchQuery.isNotEmpty() && searchQuery.isNotBlank()) {
            appLanguages.filter {
                it.displayLanguage.contains(searchQuery, ignoreCase = true) ||
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        } else {
            appLanguages
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(10.dp)
    ) {
        Text(
            stringResource(Res.string.select_language),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(stringResource(Res.string.search_here)) },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(100.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(languageList) { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedLanguageCode = language.code },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedLanguageCode == language.code,
                        onClick = { selectedLanguageCode = language.code }
                    )
                    Text(language.displayLanguage)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onLanguageSelected(selectedLanguageCode) }
        ) {
            Text(stringResource(Res.string.confirm))
        }
    }
}
