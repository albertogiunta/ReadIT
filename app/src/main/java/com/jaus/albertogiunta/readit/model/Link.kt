package com.jaus.albertogiunta.readit.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.joda.time.DateTime

@Entity
data class Link(@PrimaryKey(autoGenerate = true)
                var id: Int = 0,
                var title: String = "",
                var url: String = "",
                var timestamp: DateTime = DateTime.now(),
                var seen: Boolean = false
) {

    companion object {
        val EMPTY_LINK = ""
    }
}