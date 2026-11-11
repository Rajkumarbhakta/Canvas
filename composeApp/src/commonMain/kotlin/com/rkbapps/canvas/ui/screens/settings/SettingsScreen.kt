package com.rkbapps.canvas.ui.screens.settings

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.about
import canvas.composeapp.generated.resources.app_name
import canvas.composeapp.generated.resources.app_version
import canvas.composeapp.generated.resources.appearance
import canvas.composeapp.generated.resources.back
import canvas.composeapp.generated.resources.bengali
import canvas.composeapp.generated.resources.buy_me_a_coffee
import canvas.composeapp.generated.resources.cancel
import canvas.composeapp.generated.resources.confirm
import canvas.composeapp.generated.resources.english
import canvas.composeapp.generated.resources.french
import canvas.composeapp.generated.resources.german
import canvas.composeapp.generated.resources.github
import canvas.composeapp.generated.resources.hindi
import canvas.composeapp.generated.resources.icon
import canvas.composeapp.generated.resources.japanese
import canvas.composeapp.generated.resources.korean
import canvas.composeapp.generated.resources.language
import canvas.composeapp.generated.resources.language_description
import canvas.composeapp.generated.resources.mail
import canvas.composeapp.generated.resources.personalization
import canvas.composeapp.generated.resources.portuguese
import canvas.composeapp.generated.resources.privacy_policy
import canvas.composeapp.generated.resources.privacy_policy_desc
import canvas.composeapp.generated.resources.raise_a_issue
import canvas.composeapp.generated.resources.russian
import canvas.composeapp.generated.resources.select_language
import canvas.composeapp.generated.resources.settings
import canvas.composeapp.generated.resources.spanish
import canvas.composeapp.generated.resources.theme
import canvas.composeapp.generated.resources.theme_dark
import canvas.composeapp.generated.resources.theme_description
import canvas.composeapp.generated.resources.theme_light
import canvas.composeapp.generated.resources.theme_system
import com.rkbapps.canvas.ui.composables.drawPath
import com.rkbapps.canvas.ui.screens.drawing.composables.ChromeButton
import com.rkbapps.canvas.ui.screens.drawing.composables.ChromeShape
import com.rkbapps.canvas.ui.screens.drawing.composables.ColorSwatches
import com.rkbapps.canvas.ui.screens.drawing.utils.PaintingStyleType
import com.rkbapps.canvas.ui.theme.appColorScheme
import com.rkbapps.canvas.ui.theme.defaultColor
import com.rkbapps.canvas.util.Language
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.appLanguages
import com.rkbapps.canvas.util.getPlatform
import com.rkbapps.canvas.util.getWindowSize
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.PI
import kotlin.math.sin

// ─────────────────────────────────────────────────────────────────────────────
// SETTINGS — one grouped list on every screen size.
//
//   Personalization   appearance (system / light / dark), theme colour, language
//   About             the app, where to reach it, privacy policy
// ─────────────────────────────────────────────────────────────────────────────

/** Light or dark, or whatever the system says. Stored as two flags; this is the single choice shown. */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Theme colours offered as one-tap swatches; the default comes first. */
private val themeColors = listOf(
    defaultColor,       // ochre
    Color(0xFFC0392B),  // red
    Color(0xFFB83280),  // magenta
    Color(0xFF6750A4),  // violet
    Color(0xFF2F5FD0),  // blue
    Color(0xFF0E8A8A),  // teal
    Color(0xFF2E7D32),  // green
    Color(0xFF5D6470),  // slate
)

private const val GITHUB_URL = "https://github.com/Rajkumarbhakta/Canvas"
private const val ISSUES_URL = "https://github.com/Rajkumarbhakta/Canvas/issues"
private const val MAIL_ADDRESS = "contact@rkbapps.in"
private const val COFFEE_URL = "https://coff.ee/rajkumarbhakta"
private const val PRIVACY_URL = "https://sites.google.com/view/canvas-privacy/home"

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val isSystemTheme by viewModel.isSystemTheme.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val colorTheme by viewModel.colorTheme.collectAsStateWithLifecycle()

    SettingsContent(
        themeMode = when {
            isSystemTheme -> ThemeMode.SYSTEM
            isDarkTheme -> ThemeMode.DARK
            else -> ThemeMode.LIGHT
        },
        themeColor = Color(colorTheme),
        language = viewModel.getLocale(),
        appVersion = viewModel.appVersion,
        isWide = getWindowSize().widthSizeClass != WindowWidthSizeClass.Compact,
        onThemeModeChange = { mode ->
            viewModel.updateIsSystemTheme(mode == ThemeMode.SYSTEM)
            if (mode != ThemeMode.SYSTEM) viewModel.updateTheme(mode == ThemeMode.DARK)
        },
        onThemeColorChange = viewModel::updateColorTheme,
        onLanguageChange = { code ->
            viewModel.changeLanguage(code)
            // Outside Android the new language only shows once the screen is rebuilt.
            if (getPlatform() != Platforms.ANDROID) navController.navigateUp()
        },
        onBack = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    themeMode: ThemeMode,
    themeColor: Color,
    language: Language,
    appVersion: String,
    isWide: Boolean,
    onThemeModeChange: (ThemeMode) -> Unit,
    onThemeColorChange: (Color) -> Unit,
    onLanguageChange: (code: String) -> Unit,
    onBack: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var languageDialogOpen by rememberSaveable { mutableStateOf(false) }

    if (languageDialogOpen) {
        LanguageDialog(
            currentCode = language.code,
            onDismiss = { languageDialogOpen = false },
            onConfirm = { code ->
                languageDialogOpen = false
                if (code != language.code) onLanguageChange(code)
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    ChromeButton(
                        label = stringResource(Res.string.back),
                        onClick = onBack,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.TopCenter
        ) {
            // A comfortable reading width, centred on wide screens.
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .padding(horizontal = if (isWide) 32.dp else 16.dp)
                    .padding(top = 8.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                SettingsGroup(stringResource(Res.string.personalization)) {
                    SettingBlock(stringResource(Res.string.appearance)) {
                        ThemeModePicker(themeMode, themeColor, onThemeModeChange)
                    }
                    GroupDivider()
                    SettingBlock(
                        title = stringResource(Res.string.theme),
                        description = stringResource(Res.string.theme_description)
                    ) {
                        ColorSwatches(
                            palette = themeColors,
                            selected = themeColor,
                            onSelected = onThemeColorChange
                        )
                    }
                    GroupDivider()
                    SettingRow(
                        icon = rememberVectorPainter(Icons.Outlined.Language),
                        title = stringResource(Res.string.language),
                        value = language.displayLanguage,
                        trailing = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        onClick = { languageDialogOpen = true }
                    )
                }

                SettingsGroup(stringResource(Res.string.about)) {
                    AppIdentity(appVersion)
                    GroupDivider()
                    LinkRow(
                        rememberVectorPainter(Icons.Outlined.PrivacyTip),
                        stringResource(Res.string.privacy_policy),
                        stringResource(Res.string.privacy_policy_desc)
                    ) { openUri(uriHandler, PRIVACY_URL) }
                    GroupDivider()
                    LinkRow(
                        painterResource(Res.drawable.github),
                        stringResource(Res.string.github),
                        "Rajkumarbhakta/Canvas"
                    ) { openUri(uriHandler, GITHUB_URL) }
                    GroupDivider()
                    LinkRow(
                        rememberVectorPainter(Icons.Outlined.BugReport),
                        stringResource(Res.string.raise_a_issue)
                    ) { openUri(uriHandler, ISSUES_URL) }
                    GroupDivider()
                    LinkRow(
                        rememberVectorPainter(Icons.Outlined.Mail),
                        stringResource(Res.string.mail),
                        MAIL_ADDRESS
                    ) { openUri(uriHandler, "mailto:$MAIL_ADDRESS") }
                    GroupDivider()
                    LinkRow(
                        rememberVectorPainter(Icons.Outlined.Coffee),
                        stringResource(Res.string.buy_me_a_coffee)
                    ) { openUri(uriHandler, COFFEE_URL) }
                }
            }
        }
    }
}

// ── Group scaffolding ────────────────────────────────────────────────────────

private val GroupShape = RoundedCornerShape(20.dp)

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(
            Modifier
                .fillMaxWidth()
                .clip(GroupShape)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) { content() }
    }
}

@Composable
private fun GroupDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/** A setting whose control needs room of its own, below its title. */
@Composable
private fun SettingBlock(
    title: String,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        if (description != null) {
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.size(12.dp))
        content()
    }
}

/** One line of the list; the whole row is the target. */
@Composable
private fun SettingRow(
    icon: Painter,
    title: String,
    onClick: () -> Unit,
    description: String? = null,
    value: String? = null,
    trailing: ImageVector? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onClick)
            .heightIn(min = 64.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (description != null) {
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (value != null) {
            Text(
                value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (trailing != null) {
            Icon(
                trailing, null,
                Modifier.padding(start = 4.dp).size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** A row that leaves the app; the trailing icon says so. */
@Composable
private fun LinkRow(icon: Painter, title: String, description: String? = null, onClick: () -> Unit) {
    SettingRow(
        icon = icon,
        title = title,
        description = description,
        trailing = Icons.AutoMirrored.Rounded.OpenInNew,
        onClick = onClick
    )
}

@Composable
private fun AppIdentity(appVersion: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(Res.drawable.icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
        )
        Column(Modifier.padding(start = 16.dp)) {
            Text(
                stringResource(Res.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                stringResource(Res.string.app_version, appVersion),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Appearance ───────────────────────────────────────────────────────────────

/**
 * System, light or dark, each shown as a miniature of the drawing screen in
 * the colours that choice would give with the current theme colour.
 */
@Composable
private fun ThemeModePicker(
    selected: ThemeMode,
    themeColor: Color,
    onSelected: (ThemeMode) -> Unit,
) {
    val light = appColorScheme(themeColor, darkTheme = false)
    val dark = appColorScheme(themeColor, darkTheme = true)
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ThemeMode.entries.forEach { mode ->
            val isSelected = mode == selected
            val label = stringResource(
                when (mode) {
                    ThemeMode.SYSTEM -> Res.string.theme_system
                    ThemeMode.LIGHT -> Res.string.theme_light
                    ThemeMode.DARK -> Res.string.theme_dark
                }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(ChromeShape)
                    .selectable(selected = isSelected, role = Role.RadioButton) { onSelected(mode) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.4f)
                        .clip(ChromeShape)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                            shape = ChromeShape
                        )
                ) {
                    when (mode) {
                        ThemeMode.LIGHT -> drawMiniature(light)
                        ThemeMode.DARK -> drawMiniature(dark)
                        // Half and half: it follows whichever the device is using.
                        ThemeMode.SYSTEM -> {
                            clipRect(right = size.width / 2) { drawMiniature(light) }
                            clipRect(left = size.width / 2) { drawMiniature(dark) }
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Rounded.Check, null,
                            Modifier.padding(end = 4.dp).size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** The drawing screen in a thumbnail: top bar, desk, a sheet of paper and one stroke. */
private fun DrawScope.drawMiniature(scheme: ColorScheme) {
    val barHeight = size.height * 0.2f
    drawRect(scheme.surfaceDim)
    drawRect(scheme.surfaceContainerLow, size = Size(size.width, barHeight))
    val sheet = Rect(
        left = size.width * 0.3f,
        top = barHeight + size.height * 0.1f,
        right = size.width * 0.7f,
        bottom = size.height * 0.92f
    )
    drawRect(Color.White, sheet.topLeft, sheet.size)
    val inset = sheet.width * 0.18f
    drawPath(
        path = List(25) { i ->
            val t = i / 24f
            Offset(
                x = sheet.left + inset + (sheet.width - inset * 2) * t,
                y = sheet.center.y - sin(t * 2 * PI).toFloat() * sheet.height * 0.14f
            )
        },
        color = scheme.primary,
        thickness = 2.5.dp.toPx(),
        pathEffect = PaintingStyleType.STROKE
    )
}

// ── Language ─────────────────────────────────────────────────────────────────

/** Each language in its own script, with its name in the current language underneath. */
@Composable
private fun LanguageDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onConfirm: (code: String) -> Unit,
) {
    var selectedCode by rememberSaveable { mutableStateOf(currentCode) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.select_language)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                appLanguages.forEach { language ->
                    val isSelected = language.code == selectedCode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ChromeShape)
                            .selectable(selected = isSelected, role = Role.RadioButton) {
                                selectedCode = language.code
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = isSelected, onClick = null, Modifier.padding(horizontal = 12.dp))
                        Column {
                            Text(language.displayLanguage, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                stringResource(language.localizedName()),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(Res.string.language_description),
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedCode) }) { Text(stringResource(Res.string.confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
        }
    )
}

private fun Language.localizedName(): StringResource = when (code) {
    "ru" -> Res.string.russian
    "pt" -> Res.string.portuguese
    "es" -> Res.string.spanish
    "de" -> Res.string.german
    "fr" -> Res.string.french
    "ja" -> Res.string.japanese
    "ko" -> Res.string.korean
    "hi" -> Res.string.hindi
    "bn" -> Res.string.bengali
    else -> Res.string.english
}

/** Opens [uri]; does nothing if no app on the device can handle it (no browser or mail client). */
private fun openUri(uriHandler: UriHandler, uri: String) {
    try {
        uriHandler.openUri(uri)
    } catch (_: Exception) {
    }
}
