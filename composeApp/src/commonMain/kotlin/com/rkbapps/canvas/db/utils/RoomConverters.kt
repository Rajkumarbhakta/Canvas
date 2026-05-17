package com.rkbapps.canvas.db.utils

import androidx.room.TypeConverter
import kotlin.time.Instant

class RoomConverters {
    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun toInstant(millis: Long): Instant {
        return Instant.Companion.fromEpochMilliseconds(millis)
    }
}