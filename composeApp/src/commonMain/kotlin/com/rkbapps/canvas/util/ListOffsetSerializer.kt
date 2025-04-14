package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer

object ListOffsetSerializer : KSerializer<List<Offset>> by ListSerializer(OffsetSerializer)