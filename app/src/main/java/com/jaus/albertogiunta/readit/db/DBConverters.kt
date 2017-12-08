package com.jaus.albertogiunta.readit.db

import android.arch.persistence.room.TypeConverter
import org.joda.time.DateTime

class DBConverters {

    @TypeConverter
    fun fromTimestampToDateTime(ts: Long): DateTime = DateTime(ts)

    @TypeConverter
    fun fromDateTimeToTimestamp(dt: DateTime): Long = dt.millis

}