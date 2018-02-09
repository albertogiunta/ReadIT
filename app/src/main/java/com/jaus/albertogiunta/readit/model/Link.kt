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
        private const val SECOND = 1
        private const val MINUTE = 60
        private const val TEN_SEC = SECOND * 10
        private const val TEN_MIN = MINUTE * 10
        const val REWARD_TIME = TEN_MIN
    }
}

data class WebsiteInfo(
        var url: String,
        var title: String
)