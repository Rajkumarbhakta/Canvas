package com.rkbapps.canvas.util

import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object OffsetSerializer : KSerializer<Offset> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Offset") {
            element<Float>("x")
            element<Float>("y")
        }

    override fun serialize(encoder: Encoder, value: Offset) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeFloatElement(descriptor, 0, value.x)
        composite.encodeFloatElement(descriptor, 1, value.y)
        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Offset {
        val dec = decoder.beginStructure(descriptor)
        var x = 0f
        var y = 0f

        loop@ while (true) {
            when (val index = dec.decodeElementIndex(descriptor)) {
                0 -> x = dec.decodeFloatElement(descriptor, 0)
                1 -> y = dec.decodeFloatElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break@loop
                else -> throw SerializationException("Unexpected index: $index")
            }
        }

        dec.endStructure(descriptor)
        return Offset(x, y)
    }
}
