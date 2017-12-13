package com.jaus.albertogiunta.readit.utils

import android.content.ClipData
import android.content.Context
import java.net.URL

object SystemUtils {

    fun getURLFromClipboard(context: Context): String {
        val clipboard = context.clipboard()
        return clipboard.primaryClip.getItemAt(0).text.toString()
    }

    fun saveURLToClipboard(context: Context) {
        val url = "https://www.facebook.com/pg/CrossfitPesaro/posts/?ref=page_internal"
        val clip = ClipData.newPlainText("url", url)
        context.clipboard().primaryClip = clip
    }

    fun getHost(url: String): String = URL(url).host

}