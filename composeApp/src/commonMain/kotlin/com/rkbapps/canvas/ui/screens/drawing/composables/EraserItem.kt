package com.rkbapps.canvas.ui.screens.drawing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import canvas.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EraserItem(
    isEraserSelected: Boolean,
    onClick: () -> Unit,
){
    Row (modifier = Modifier
        .background(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(10.dp))
        .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)){
        PaintingStyleItem(
            isSelected = isEraserSelected,
            icon = {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(Res.drawable.ink_erase),
                    contentDescription = stringResource(Res.string.erase)
                )
            },
            title = {
                Text(stringResource(Res.string.erase), style = MaterialTheme.typography.labelSmall)
            }
        ) {
            onClick()
        }

    }
}