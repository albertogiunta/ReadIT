package com.jaus.albertogiunta.readit.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * VIEWS
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}


/**
 * RETROFIT
 */
fun ResponseBody.toJsoupDocument(): Document {
    return Jsoup.parse(this.byteStream(), "UTF-8", "")
}