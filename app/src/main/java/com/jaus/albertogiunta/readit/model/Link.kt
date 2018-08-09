package com.jaus.albertogiunta.readit.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.jaus.albertogiunta.readit.BuildConfig
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
        val IS_ALL_LINKS_DEBUG_ACTIVE = if (!BuildConfig.DEBUG) BuildConfig.DEBUG else false
        const val EMPTY_LINK = ""
    }
}

data class WebsiteInfo(
        var url: String,
        var title: String
)