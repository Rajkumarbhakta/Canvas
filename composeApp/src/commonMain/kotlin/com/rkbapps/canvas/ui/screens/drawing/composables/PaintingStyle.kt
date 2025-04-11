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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class PaintingStyleType{
    STROKE,
    DOT,
    FILL
}

data class PaintingStyle(
    val title:String,
    val icon: ImageVector,
    val type:PaintingStyleType
)

private val paintingStyles = listOf(
    PaintingStyle(
        title = "Stroke",
        icon = Icons.Default.HorizontalRule,
        type = PaintingStyleType.STROKE
    ),
    PaintingStyle(
        title = "Dot",
        icon = Icons.Default.MoreHoriz,
        type = PaintingStyleType.DOT
    ),
    PaintingStyle(
        title = "Fill",
        icon = Icons.Default.RadioButtonChecked,
        type = PaintingStyleType.FILL
    )
)


@Composable
fun PaintingStyle(
    selected:PaintingStyleType = PaintingStyleType.STROKE,
    onSelected:(PaintingStyleType)->Unit = {}
){
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        paintingStyles.forEach {
            PaintingStyleItem(
                isSelected = selected==it.type,
                icon = {
                    Icon(imageVector = it.icon, contentDescription = it.title, modifier = Modifier.size(20.dp))
                },
                title = {
                    Text(it.title, style = MaterialTheme.typography.labelSmall)
                }
            ) {
                onSelected(it.type)
            }
        }
    }
}

@Composable
fun PaintingStyleItem(
    isSelected: Boolean = false,
    icon: @Composable ()->Unit,
    title: @Composable ()->Unit,
    onClick: ()->Unit
){
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

