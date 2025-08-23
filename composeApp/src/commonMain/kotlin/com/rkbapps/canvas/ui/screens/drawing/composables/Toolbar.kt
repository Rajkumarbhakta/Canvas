package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    onBrushSelected: () -> Unit,
    onEraserSelected: () -> Unit,
    onShapeSelected: () -> Unit,
    onLayersSelected: () -> Unit,
    onColorPickerSelected: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val isMobile = maxWidth < 600.dp  // breakpoint: small screen vs large screen

        if (isMobile) {
            // -------- Mobile: FAB + Dropdown Menu --------
            var expanded by remember { mutableStateOf(false) }

            Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Brush") },
                        onClick = { onBrushSelected(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Brush, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Eraser") },
                        onClick = { onEraserSelected(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Delete, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Shape") },
                        onClick = { onShapeSelected(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Circle, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Layers") },
                        onClick = { onLayersSelected(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Layers, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Color Picker") },
                        onClick = { onColorPickerSelected(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.ColorLens, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Undo") },
                        onClick = { onUndo(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Undo, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Redo") },
                        onClick = { onRedo(); expanded = false },
                        leadingIcon = { Icon(Icons.Default.Redo, null) }
                    )
                }
            }
        } else {
            // -------- Desktop/Web: Horizontal Row --------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBrushSelected) {
                    Icon(Icons.Default.Brush, contentDescription = "Brush")
                }
                IconButton(onClick = onEraserSelected) {
                    Icon(Icons.Default.Delete, contentDescription = "Eraser")
                }
                IconButton(onClick = onShapeSelected) {
                    Icon(Icons.Default.Circle, contentDescription = "Shape")
                }
                IconButton(onClick = onLayersSelected) {
                    Icon(Icons.Default.Layers, contentDescription = "Layers")
                }
                IconButton(onClick = onColorPickerSelected) {
                    Icon(Icons.Default.ColorLens, contentDescription = "Color Picker")
                }
                IconButton(onClick = onUndo) {
                    Icon(Icons.Default.Undo, contentDescription = "Undo")
                }
                IconButton(onClick = onRedo) {
                    Icon(Icons.Default.Redo, contentDescription = "Redo")
                }
            }
        }
    }
}
