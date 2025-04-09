package com.rkbapps.canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rkbapps.canvas.util.Constants
import com.rkbapps.canvas.util.Platforms
import com.rkbapps.canvas.util.getPlatform
import com.rkbapps.canvas.viewmodels.DrawingAction
import com.rkbapps.canvas.viewmodels.DrawingViewModel

@Composable
fun DrawingScreen(viewModel: DrawingViewModel = if (getPlatform() == Platforms.ANDROID) viewModel() else DrawingViewModel()){
    val state = viewModel.state.collectAsStateWithLifecycle()
    Scaffold {
        Column(Modifier.fillMaxSize().padding(it).padding(vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DrawingCanvas(
                paths = state.value.paths,
                currentPath = state.value.currentPath,
                onAction = viewModel::onAction,
                modifier = Modifier.fillMaxWidth().weight(1f)
            )
            Row(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Constants.colors.forEach {
                    ColorItems(
                        color = it,
                        isSelected = it == state.value.selectedColor
                    ){
                        viewModel.onAction(DrawingAction.OnSelectColor(it))
                    }
                }
            }

            Slider(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                value = state.value.selectedThickness,
                onValueChange = {
                    viewModel.onAction(DrawingAction.OnThicknessChange(it))
                },
                valueRange = 1f..50f,
            )

            Button(
                modifier = Modifier.fillMaxWidth(0.6f).align(Alignment.CenterHorizontally),
                onClick = {
                    viewModel.onAction(DrawingAction.OnClearCanvasList)
                }
            ) {
                Text("Clear Canvas")
            }

        }
    }
}

@Composable
fun ColorItems(
    color:Color,
    isSelected: Boolean,
    onClick: (color:Color) -> Unit = {}
){
    Box(modifier = Modifier
        .clip(CircleShape)
        .background(color = color)
        .size(50.dp)
        .clickable{
            onClick(color)
        }
        .border(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) {
                if(isSystemInDarkTheme() || color == Color.Black){
                    Color.White
                }else{
                    Color.Black
                }
            }else Color.Transparent,
            shape = CircleShape
        )
    )
}