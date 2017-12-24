package com.jaus.albertogiunta.readit

import android.app.Application
import android.arch.persistence.room.Room
import com.jaus.albertogiunta.readit.db.AppDatabase
import com.jaus.albertogiunta.readit.db.AppDatabase.Companion.DB_NAME
import com.jaus.albertogiunta.readit.db.AppDatabase.Companion.IS_DB_DEBUG_ACTIVE
import com.jaus.albertogiunta.readit.notifications.NotificationBuilder
import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.doAsync

class MyApplication: Application() {

    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // setup database
        if (IS_DB_DEBUG_ACTIVE) this.deleteDatabase(DB_NAME)
        database = Room.databaseBuilder(this, AppDatabase::class.java, DB_NAME).build()
        doAsync {
            if (IS_DB_DEBUG_ACTIVE) database.linkDao().deleteAll()
        }

        // init all the things
        JodaTimeAndroid.init(this)
        NotificationBuilder.init(this)
    }
}