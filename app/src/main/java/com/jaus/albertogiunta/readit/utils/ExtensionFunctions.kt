package com.jaus.albertogiunta.readit.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.model.Link
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_link_1.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime
import org.joda.time.Period
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * VIEWS
 */

fun View.toggleVisibility() {
    if (this.visibility == View.VISIBLE) this.gone() else this.visible()
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

inline fun View.consumeEditButton(f: () -> Unit) {
    f()
    this.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    this.gone()
}

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun <T : RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
    itemView.setOnClickListener {
        event.invoke(it, adapterPosition, itemViewType)
    }
    return this
}

fun <T : RecyclerView.ViewHolder> T.onLongClick(event: (view: View, position: Int, type: Int) -> Unit): T {
    itemView.setOnLongClickListener {
        event.invoke(it, adapterPosition, itemViewType)
        true
    }
    return this
}

fun ImageView.loadFavicon(url: String) {
    try {
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_placeholder)
                .into(ivFavicon)
    } catch (e: Exception) {
        println("ERROR in PICASSO!!! $e")
    }
}

fun Context.clipboard(): ClipboardManager = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

fun Context.clearClipboard() {
    clipboard().primaryClip = ClipData.newPlainText("", Link.EMPTY_LINK)
}

fun Context.hasItemInClipboard(): Boolean = clipboard().primaryClip != null

fun Context.getURLFromClipboard(): String? {
    val clipboard = clipboard()
    return if (clipboard.primaryClip != null) clipboard.primaryClip.getItemAt(0).text.toString() else null
}

fun Context.saveURLToClipboard(url: String) {
    clipboard().primaryClip = ClipData.newPlainText("url", url)
}

fun DateTime.getRemainingTime() = Period(this.plusDays(1), DateTime.now()).toCustomString(false)

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
    doAsync {
        dao.insert(this@addTo)
        val linkId = dao.getMaxId()
        linkList.add(0, this@addTo.apply { id = linkId })
    }
}

fun Link.update(dao: LinkDao, linkList: MutableList<Link>, position: Int) {
    doAsync { dao.update(this@update) }
    linkList[position] = this
}

fun Link.remove(dao: LinkDao, linkList: MutableList<Link>, position: Int) {
    doAsync { dao.delete(this@remove) }
    linkList.removeAt(position)
}

fun Link.faviconURL() = "https://${Utils.getHostOfURL(this.url)}/favicon.ico"

fun Period.toCustomString(verbose: Boolean): String {
    var timeString = ""
    val h = Math.abs(hours)
    val m = Math.abs(minutes)

    when (verbose) {
        true -> {
            if (h != 0) {
                timeString += "$h hour"
                if (h != 1) timeString += "s"
            }

            if (m != 0) {
                if (h != 0) timeString += " and "
                timeString += "$m minute"
                if (m != 1) timeString += "s"
            }
        }
        false -> {
            if (h != 0) timeString += "${h}h "
            if (m != 0) timeString += "${m}m"
        }
    }

    timeString += " left"

    return timeString
}