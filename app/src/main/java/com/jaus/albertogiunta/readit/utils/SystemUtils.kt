package com.jaus.albertogiunta.readit.utils

import android.content.ClipData
import android.content.Context

object SystemUtils {

    fun getURLFromClipboard(context: Context): String {
        saveURLToClipboard(context)
        val clipboard = context.clipboard()
        return clipboard.primaryClip.getItemAt(0).text.toString()
    }

    fun saveURLToClipboard(context: Context) {
        val url = "https://www.facebook.com/pg/CrossfitPesaro/posts/?ref=page_internal"
        val clip = ClipData.newPlainText("url", url)
        context.clipboard().primaryClip = clip
    }


}