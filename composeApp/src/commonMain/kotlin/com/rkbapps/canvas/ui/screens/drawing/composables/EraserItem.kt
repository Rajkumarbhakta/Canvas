package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.erase
import canvas.composeapp.generated.resources.ink_erase
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EraserItem(
    isEraserSelected: Boolean,
    showLabel: Boolean = false,
    onClick: () -> Unit,
) {
    ToolButton(
        isActive = isEraserSelected,
        label = stringResource(Res.string.erase),
        showLabel = showLabel,
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(22.dp),
            painter = painterResource(Res.drawable.ink_erase),
            contentDescription = stringResource(Res.string.erase)
        )
    }
}