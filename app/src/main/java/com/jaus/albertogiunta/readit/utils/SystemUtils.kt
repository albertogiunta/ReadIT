package com.jaus.albertogiunta.readit.utils

import android.content.ClipData
import android.content.Context
import com.jaus.albertogiunta.readit.model.Link
import java.net.URL

object SystemUtils {

    fun clearClipboard(context: Context) {
        val clipboard = context.clipboard()
        clipboard.primaryClip = ClipData.newPlainText("", Link.EMPTY_LINK)
    }

    fun hasItemInClipboard(context: Context): Boolean {
        val clipboard = context.clipboard()
        return clipboard.primaryClip != null
    }

    fun getURLFromClipboard(context: Context): String? {
        val clipboard = context.clipboard()
        return if (clipboard.primaryClip != null) clipboard.primaryClip.getItemAt(0).text.toString() else null
    }

    fun saveURLToClipboard(context: Context) {
        val url = "https://www.facebook.com/pg/CrossfitPesaro/posts/?ref=page_internal"
        val clip = ClipData.newPlainText("url", url)
        context.clipboard().primaryClip = clip
    }

    fun getHost(url: String): String = URL(url).host

}