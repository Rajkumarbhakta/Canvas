package com.rkbapps.canvas.util

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.text.toInt

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)

    override fun deserialize(decoder: Decoder): Color {
        return Color(decoder.decodeInt())
    }

    override fun serialize(
        encoder: Encoder,
        value: Color
    ) {
        encoder.encodeInt(value.value.toInt())
    }

}