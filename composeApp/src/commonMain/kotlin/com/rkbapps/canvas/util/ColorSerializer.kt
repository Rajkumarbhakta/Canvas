package com.rkbapps.canvas.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.text.toInt

object ColorSerializer : KSerializer<Color> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Color", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Color {
        val hexString = decoder.decodeString()
        return hexToColor(hexString)
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun serialize(
        encoder: Encoder,
        value: Color
    ) {
        encoder.encodeString(value.toHex())
    }

}

fun Color.toHex(withAlpha: Boolean = true): String {
    val r = (red * 255).toInt().toString(16).padStart(2, '0')
    val g = (green * 255).toInt().toString(16).padStart(2, '0')
    val b = (blue * 255).toInt().toString(16).padStart(2, '0')
    val a = (alpha * 255).toInt().toString(16).padStart(2, '0')

    return if (withAlpha) "#$a$r$g$b" else "#$r$g$b"
}

fun hexToColor(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return when (cleanHex.length) {
        6 -> Color(
            red = cleanHex.substring(0, 2).toInt(16),
            green = cleanHex.substring(2, 4).toInt(16),
            blue = cleanHex.substring(4, 6).toInt(16)
        )
        8 -> Color(
            alpha = cleanHex.substring(0, 2).toInt(16),
            red = cleanHex.substring(2, 4).toInt(16),
            green = cleanHex.substring(4, 6).toInt(16),
            blue = cleanHex.substring(6, 8).toInt(16)
        )
        else -> error("Invalid hex color format: $hex")
    }
}

