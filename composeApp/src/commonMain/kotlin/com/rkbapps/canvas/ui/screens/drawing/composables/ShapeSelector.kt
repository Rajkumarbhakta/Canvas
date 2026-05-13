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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.outlined.FormatShapes
import androidx.compose.material.icons.outlined.Hexagon
import androidx.compose.material.icons.outlined.Pentagon
import androidx.compose.material.icons.outlined.Rectangle
import androidx.compose.material.icons.outlined.Square
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

data class ShapeOption(
    val type: ShapeType,
    val icon: ImageVector,
    val title: StringResource
)


val shapeOptions = listOf(
    ShapeOption(
        type = ShapeType.NONE,
        icon = Icons.Filled.Close,
        title = Res.string.shape_none
    ),
    ShapeOption(
        type = ShapeType.LINE,
        icon = Icons.Filled.HorizontalRule,
        title = Res.string.shape_line
    ),
    ShapeOption(
        type = ShapeType.RECTANGLE,
        icon = Icons.Outlined.Rectangle,
        title = Res.string.shape_rectangle
    ),
    ShapeOption(
        type = ShapeType.SQUARE,
        icon = Icons.Outlined.Square,
        title = Res.string.shape_square
    ),
    ShapeOption(
        type = ShapeType.CIRCLE,
        icon = Icons.Outlined.Circle,
        title = Res.string.shape_circle
    ),
    ShapeOption(
        type = ShapeType.TRIANGLE,
        icon = Icons.Outlined.ChangeHistory,
        title = Res.string.shape_triangle
    ),
    ShapeOption(
        type = ShapeType.ARROW_LEFT,
        icon = Icons.AutoMirrored.Outlined.ArrowBack,
        title = Res.string.shape_arrow_left
    ),
    ShapeOption(
        type = ShapeType.ARROW_RIGHT,
        icon = Icons.AutoMirrored.Outlined.ArrowForward,
        title = Res.string.shape_arrow_right
    ),
    ShapeOption(
        type = ShapeType.ARROW_UP,
        icon = Icons.Outlined.ArrowUpward,
        title = Res.string.shape_arrow_up
    ),
    ShapeOption(
        type = ShapeType.ARROW_DOWN,
        icon = Icons.Outlined.ArrowDownward,
        title = Res.string.shape_arrow_down
    ),
    ShapeOption(
        type = ShapeType.STAR,
        icon = Icons.Outlined.Star,
        title = Res.string.shape_star
    ),
    ShapeOption(
        type = ShapeType.PENTAGON,
        icon = Icons.Outlined.Pentagon,
        title = Res.string.shape_pentagon
    ),
    ShapeOption(
        type = ShapeType.HEXAGON,
        icon = Icons.Outlined.Hexagon,
        title = Res.string.shape_hexagon
    )
)

@Composable
fun ShapeSelector(
    selectedShape: ShapeType = ShapeType.NONE,
    onShapeSelected: (ShapeType) -> Unit
) {

    var isShapeDialogVisible by remember { mutableStateOf(false) }

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

            if (isShapeDialogVisible){
                ShapeSelectorDialog(
                    selected = selectedShape,
                    onDismissDialog = {
                        isShapeDialogVisible = false
                    }
                ){
                    onShapeSelected(it)
                    isShapeDialogVisible = false
                }
            }

            ShapeSelectorItem(
                isSelected = selectedShape!= ShapeType.NONE,
                icon = {
                    Icon(
                        imageVector = vectorResource(Res.drawable.shapes),
                        contentDescription = stringResource(Res.string.shapes)
                    )
                },
                title = {
                    Text(
                            text = stringResource(Res.string.shapes),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp
                        )
                }
            ){
                isShapeDialogVisible = true
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


@Composable
fun ShapeSelectorDialog(
    selected: ShapeType,
    onDismissDialog:()-> Unit,
    onSelected:(ShapeType)-> Unit
){
    Dialog(
        onDismissRequest = onDismissDialog
    ){
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(stringResource(Res.string.choose_shape), style = MaterialTheme.typography.headlineSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ){
                items(
                    items = shapeOptions
                ){ shape->
                    ShapeSelectorItem(
                        isSelected = selected == shape.type,
                        icon = {
                            Icon(
                                imageVector = shape.icon,
                                contentDescription = stringResource(shape.title),
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        title = {
                            Text(
                                text = stringResource(shape.title),
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp
                            )
                        },
                        onClick = { onSelected(shape.type) }
                    )
                }
            }


        }






    }



}