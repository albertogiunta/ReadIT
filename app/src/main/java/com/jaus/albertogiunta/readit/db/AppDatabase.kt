package com.jaus.albertogiunta.readit.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.jaus.albertogiunta.readit.BuildConfig
import com.jaus.albertogiunta.readit.model.Link

@Database(entities = arrayOf(Link::class), version = 1, exportSchema = false)
@TypeConverters(DBConverters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        val DB_NAME = "readit.db"
        val IS_DB_DEBUG_ACTIVE = if (!BuildConfig.DEBUG) BuildConfig.DEBUG else false
    }

    abstract fun linkDao(): LinkDao

}