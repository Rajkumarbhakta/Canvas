package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule


val appSerializerModule =  SerializersModule{
    contextual(Color::class, ColorSerializer)
    contextual(Offset::class, OffsetSerializer)
}

val json = Json {
    serializersModule = appSerializerModule
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}