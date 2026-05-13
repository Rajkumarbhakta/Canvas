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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

enum class PaintingStyleType{
    STROKE,
    DOT,
    FILL,
    SCALLOP,
    PENCIL,
}

data class PaintingStyle(
    val title:StringResource,
    val icon: ImageVector,
    val type:PaintingStyleType
)

private val paintingStyles = listOf(
    PaintingStyle(
        title = Res.string.brush_style_stroke,
        icon = Icons.Default.HorizontalRule,
        type = PaintingStyleType.STROKE
    ),
    PaintingStyle(
        title = Res.string.brush_style_dot,
        icon = Icons.Default.MoreHoriz,
        type = PaintingStyleType.DOT
    ),
    PaintingStyle(
        title = Res.string.brush_style_fill,
        icon = Icons.Default.RadioButtonChecked,
        type = PaintingStyleType.FILL
    ),
    PaintingStyle(
        title = Res.string.brush_style_scallop,
        icon = Icons.Default.Waves,
        type = PaintingStyleType.SCALLOP
    ),
    PaintingStyle(
        title = Res.string.brush_style_pencil,
        icon = Icons.Default.Edit,
        type = PaintingStyleType.PENCIL
    ),
)


@Composable
fun PaintingStyle(
    selected:PaintingStyleType = PaintingStyleType.STROKE,
    onSelected:(PaintingStyleType)->Unit = {}
){

    var isPaintingStyleDialogVisible by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (isPaintingStyleDialogVisible){
            PaintingStyleDialog(
                selected = selected,
                onDismissDialog = {
                    isPaintingStyleDialogVisible = false
                }
            ){
                onSelected(it)
                isPaintingStyleDialogVisible = false
            }
        }

        PaintingStyleItem(
            isSelected = false,
            icon = {
                Icon(imageVector = vectorResource(resource = Res.drawable.outline_stylus_brush), contentDescription = "", modifier = Modifier.size(20.dp))

            },
            title = {
                Text(stringResource(Res.string.brush), style = MaterialTheme.typography.labelSmall)
            }
        ){
            isPaintingStyleDialogVisible = true
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


@Composable
fun PaintingStyleDialog(
    selected: PaintingStyleType,
    onDismissDialog:()-> Unit,
    onSelected:(PaintingStyleType)-> Unit
){
    Dialog(
        onDismissRequest = onDismissDialog
    ){

        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(stringResource(Res.string.choose_brush_style), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                items(
                    items = paintingStyles
                ){
                    PaintingStyleItem(
                        isSelected = selected==it.type,
                        icon = {
                            Icon(imageVector = it.icon, contentDescription = stringResource(it.title), modifier = Modifier.size(20.dp))
                        },
                        title = {
                            Text(stringResource(it.title), style = MaterialTheme.typography.labelSmall)
                        }
                    ) {
                        onSelected(it.type)
                    }
                }
            }

        }
    }
}

