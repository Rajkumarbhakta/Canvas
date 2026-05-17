package com.rkbapps.canvas.util.serializers


import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object SafeOffsetListSerializer : KSerializer<List<Offset>> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor =
        buildSerialDescriptor("SafeOffsetList", StructureKind.LIST)

    override fun serialize(encoder: Encoder, value: List<Offset>) {
        // NEW COMPACT FORMAT (STRING)
        val sb = StringBuilder(value.size * 10)
        value.forEach {
            sb.append(it.x).append(',').append(it.y).append(';')
        }
        encoder.encodeString(sb.toString())
    }

    override fun deserialize(decoder: Decoder): List<Offset> {
        return when (decoder) {
            is JsonDecoder -> {
                when (val element = decoder.decodeJsonElement()) {
                    is JsonArray -> {
                        // 🔹 OLD FORMAT SUPPORT
                        element.mapNotNull {
                            val obj = it.jsonObject
                            val x = obj["x"]?.jsonPrimitive?.floatOrNull
                            val y = obj["y"]?.jsonPrimitive?.floatOrNull
                            if (x != null && y != null) Offset(x, y) else null
                        }
                    }

                    is JsonPrimitive -> {
                        // 🔹 NEW COMPACT FORMAT
                        val str = element.content
                        if (str.isBlank()) emptyList()
                        else str.split(';')
                            .filter { it.isNotEmpty() }
                            .map {
                                val (x, y) = it.split(',')
                                Offset(x.toFloat(), y.toFloat())
                            }
                    }

                    else -> emptyList()
                }
            }
            else -> emptyList()
        }
    }
}
