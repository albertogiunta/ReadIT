package com.jaus.albertogiunta.readit.utils

import android.content.*
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import com.google.firebase.analytics.FirebaseAnalytics
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.db.Settings
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.viewPresenter.linksHome.CardLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_link_1.view.*
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.joda.time.DateTime
import org.joda.time.Period
import org.joda.time.format.PeriodFormatterBuilder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


/**
 * VIEW
 */

fun View.toggleVisibility(setAsVisible: Boolean) = if (setAsVisible) this.visible() else this.gone()

fun View.toggleVisibility() = if (this.visibility == View.VISIBLE) this.gone() else this.visible()

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

inline fun MenuItem.consumeOptionButton(f: () -> Unit): Boolean {
    f()
    return true
}

fun Menu.toggleSeen(displaySeenLinks: Boolean) =
    this.findItem(R.id.action_toggle_seen).setIcon(if (displaySeenLinks) R.drawable.ic_seen_enabled else R.drawable.ic_seen_disabled)

fun Menu.togglePreferredCardRadioButton() =
    this.findItem(Settings.cardLayout.action).setIcon(R.drawable.ic_radio_button_checked)

fun Menu.toggleCardsRadioButtons(preferredCard: CardLayout = Settings.cardLayout) =
    CardLayout.values().forEach {
        when (preferredCard.action) {
            it.action -> this.findItem(it.action).setIcon(R.drawable.ic_radio_button_checked)
            else -> this.findItem(it.action).setIcon(R.drawable.ic_radio_button_unchecked)
        }
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

/**
 * CONTEXT
 */
fun Context.clipboard(): ClipboardManager =
    this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

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

fun Context.openPlayStore() {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
    } catch (anfe: ActivityNotFoundException) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            )
        )
    }
}

fun Context.sendFirebaseEvent(contentType: FirebaseContentType, action: FirebaseAction) {
    val bundle = Bundle()
    with(bundle) {
        putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType.description)
        putString(FirebaseAnalytics.Param.ITEM_ID, action.description)
    }

    FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
}

/**
 * STRING
 */
fun String.hasProtocol() = this.indexOf("http") != -1

fun String.cleanFromSpaces() = this.replace("\\s+".toRegex(), "")

fun String.substringAtProtocol() = this.substring(indexOf("http")).cleanFromSpaces()

fun String.addProtocol() = "http://" + this.cleanFromSpaces()

fun String.polished() = if (this.hasProtocol()) this.substringAtProtocol() else this.addProtocol()

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
    }.get()
}

fun Link.update(dao: LinkDao, linkList: MutableList<Link>, position: Int) {
    linkList[position] = this
    doAsync { dao.update(this@update) }.get()
}

fun Link.remove(dao: LinkDao, linkList: MutableList<Link>, position: Int) {
    linkList.removeAt(position)
    doAsync { dao.delete(this@remove) }.get()
}

fun Link.faviconURL() = "https://${Utils.getHostOfURL(this.url)}/favicon.ico"

fun Link.notificationString(): String {
    val customTitle = if (this.title.length >= 50) Pair(
        this.title.substring(0, 45) + "...",
        this.timestamp
    ) else this.title
    return "⌛️ ${this.timestamp.getRemainingTime().toHHmm()} ➡️ $customTitle\n"
}

fun List<Link>.filterAndSortForLinksActivity(): List<Link> {
    val showSeen = Settings.showSeen
    return if (!Link.IS_ALL_LINKS_DEBUG_ACTIVE)
        this.filter { it.timestamp.isNotExpired24h() && !it.seen || (showSeen && it.seen) }.sortedWith(
            compareBy(Link::seen, Link::timestamp)
        )
    else
        this.filter { !it.seen || (showSeen && it.seen) }.sortedWith(
            compareBy(
                Link::seen,
                Link::timestamp
            )
        )
}

fun List<Link>.filterAndSortForNotification() =
    if (!Link.IS_ALL_LINKS_DEBUG_ACTIVE)
        this.reversed().filter { !it.seen && it.timestamp.isNotExpired24h() }
    else
        this.reversed().filter { !it.seen }

/**
 * DATETIME
 */
fun DateTime.getRemainingTime() = Period(this.plusDays(1), DateTime.now())

fun DateTime.isNotExpired24h(): Boolean = this.isAfter(DateTime.now().minusHours(24))

fun Period.toLiteralString(verbose: Boolean): String {
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

fun Period.toHHmm(): String {
    return with(PeriodFormatterBuilder()) {
        printZeroAlways()
        minimumPrintedDigits(2)
        appendHours()
        appendSeparator(":")
        appendMinutes()
        toFormatter()
    }.print(this).replace("-", "")
}