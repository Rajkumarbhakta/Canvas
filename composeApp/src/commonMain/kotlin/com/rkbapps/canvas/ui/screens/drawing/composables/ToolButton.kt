package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A professional drawing tool button that follows M3 Expressive guidelines.
 *
 * - **Active**: Filled primary-container background with spring scale pop
 * - **Inactive**: Tonal surface-variant background
 *
 * @param isActive Whether this tool is currently selected
 * @param label Optional short label shown below icon (e.g. "Brush", "Eraser")
 * @param showLabel Whether to show the label text (hide on compact rail)
 * @param modifier Modifier
 * @param onClick Click handler
 * @param content The icon content
 */
@Composable
fun ToolButton(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    label: String? = null,
    showLabel: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ToolButtonScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.scale(scale),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                FilledIconButton(
                    onClick = onClick,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                ) {
                    content()
                }
            } else {
                FilledTonalIconButton(
                    onClick = onClick,
                    modifier = Modifier.size(48.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                ) {
                    content()
                }
            }
        }
        if (showLabel && label != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

/**
 * A compact (40dp) ToolButton variant for use in mobile toolbars.
 */
@Composable
fun CompactToolButton(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CompactToolButtonScale"
    )

    Box(
        modifier = modifier.scale(scale),
        contentAlignment = Alignment.Center
    ) {
        if (isActive) {
            FilledIconButton(
                onClick = onClick,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                content()
            }
        } else {
            FilledTonalIconButton(
                onClick = onClick,
                modifier = Modifier.size(40.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            ) {
                content()
            }
        }
    }
}
