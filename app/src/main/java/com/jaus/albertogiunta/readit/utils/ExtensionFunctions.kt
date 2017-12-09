package com.jaus.albertogiunta.readit.utils

import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * VIEWS
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun Context.clipboard(): ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

/**
 * RETROFIT
 */
fun ResponseBody.toJsoupDocument(): Document {
    return Jsoup.parse(this.byteStream(), "UTF-8", "")
}

/**
 * MODEL
 */
fun Link.addTo(dao: LinkDao, linkList: MutableList<Link>) {
    doAsync { dao.insert(this@addTo) }
    linkList.add(this@addTo)
}