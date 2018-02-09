package com.jaus.albertogiunta.readit.utils

import com.jaus.albertogiunta.readit.BuildConfig
import java.net.URL


object Utils {

    const val dateTimeFormatISO8601 = "yyyy-MM-dd HH:mm:ss"

    fun getHostOfURL(url: String): String = URL(url).host

    fun ifAtLeast(version: Int, f: () -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= version) f()
    }

    fun atLeast(version: Int): Boolean = android.os.Build.VERSION.SDK_INT >= version

    fun isAdsDebugActive(): Boolean = if (!BuildConfig.DEBUG) BuildConfig.DEBUG else true

}

enum class FirebaseContentType(val description: String) {

    LINK_INTERACTION("link_interaction")

}

enum class FirebaseAction(val description: String) {

    LINK_ADD("link_add"),
    LINK_REMOVE("link_delete"),
    LINK_UPDATE("link_edit"),
    LINK_BROWSE("link_browse"),
    LINK_SHARE("link_share"),
    LINK_COPY("link_copy")

}