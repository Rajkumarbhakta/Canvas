package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShapeOption(
    val type: ShapeType,
    val icon: ImageVector,
    val title: String
)

@Composable
fun ShapeSelector(
    selectedShape: ShapeType = ShapeType.NONE,
    shapes: List<ShapeOption>,
    onShapeSelected: (ShapeType) -> Unit
) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            shapes.forEach { shape ->
                ShapeSelectorItem(
                    isSelected = selectedShape == shape.type,
                    icon = {
                        Icon(
                            imageVector = shape.icon,
                            contentDescription = shape.title,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    title = {
                        Text(
                            text = shape.title,
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp
                        )
                    },
                    onClick = { onShapeSelected(shape.type) }
                )
            }
        }
    }
}

@Composable
fun ShapeSelectorItem(
    isSelected: Boolean = false,
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width = 70.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp) )
            .border(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width =if(isSelected) 2.dp else 0.dp,
                    color = if(isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent
                )
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(4.dp))
            icon()
            title()
            Spacer(Modifier.height(4.dp))
        }
    }
}
// Remove imports for horizontalScroll and rememberScrollState