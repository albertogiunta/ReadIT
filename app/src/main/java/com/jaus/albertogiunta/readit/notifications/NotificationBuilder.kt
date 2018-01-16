package com.jaus.albertogiunta.readit.notifications

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.jaus.albertogiunta.readit.BuildConfig
import com.jaus.albertogiunta.readit.MyApplication
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.db.LinkDao
import com.jaus.albertogiunta.readit.db.Settings
import com.jaus.albertogiunta.readit.model.Link
import com.jaus.albertogiunta.readit.utils.Utils.atLeast
import com.jaus.albertogiunta.readit.utils.filterAndSortForNotification
import com.jaus.albertogiunta.readit.utils.getRemainingTime
import com.jaus.albertogiunta.readit.utils.toHHmm
import com.jaus.albertogiunta.readit.viewPresenter.linksHome.LinksActivity
import org.jetbrains.anko.doAsync
import java.util.concurrent.atomic.AtomicBoolean


@Suppress("JoinDeclarationAndAssignment")
class NotificationBuilder private constructor(ctx: Context) {

    private val context: Context
    private val dao: LinkDao
    private val linkList = mutableListOf<Link>()
    private val notificationManager: NotificationManagerCompat
    private val channelBuilder: NotificationChannelBuilder
    private val createChannel: ((channelId: String) -> NotificationChannel?)

    init {
        context = ctx.applicationContext
        dao = MyApplication.database.linkDao()
        notificationManager = NotificationManagerCompat.from(context)
        channelBuilder = NotificationChannelBuilder(context, CHANNEL_IDS)

        @TargetApi(Build.VERSION_CODES.O)
        createChannel = { channelId ->
            when (channelId) {
                IMPORTANT_CHANNEL_ID -> NotificationChannel(channelId,
                        context.getString(R.string.important_channel_name),
                        NotificationManager.IMPORTANCE_HIGH).apply {
                    description = context.getString(R.string.important_channel_description)
                }
                NORMAL_CHANNEL_ID -> NotificationChannel(channelId,
                        context.getString(R.string.normal_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = context.getString(R.string.normal_channel_description)
                }
                LOW_CHANNEL_ID -> NotificationChannel(channelId,
                        context.getString(R.string.low_channel_name),
                        NotificationManager.IMPORTANCE_LOW).apply {
                    description = context.getString(R.string.low_channel_description)
                }
                else -> null
            }
        }
    }

    companion object {
        val GROUP_KEY = "Links"
        var NOTIFICATION_ID = 0
        private const val IMPORTANT_CHANNEL_ID = "IMPORTANT_CHANNEL_ID"
        private const val NORMAL_CHANNEL_ID = "NORMAL_CHANNEL_ID"
        private const val LOW_CHANNEL_ID = "LOW_CHANNEL_ID"
        private val CHANNEL_IDS =
                if (atLeast(Build.VERSION_CODES.O)) listOf(IMPORTANT_CHANNEL_ID, NORMAL_CHANNEL_ID, LOW_CHANNEL_ID)
                else listOf(NORMAL_CHANNEL_ID)


        @SuppressLint("StaticFieldLeak")
        private lateinit var INSTANCE: NotificationBuilder
        private val initialized = AtomicBoolean()

        /**
         * To be called in Application.onCreate()
         */
        fun init(context: Context) {
            if (!initialized.getAndSet(true)) {
                INSTANCE = NotificationBuilder(context.applicationContext)
            }
        }

        /**
         * A Singleton instance of NotificationBuilder
         */
        val instance: NotificationBuilder get() = INSTANCE
    }

    fun sendBundledNotification() {
        with(notificationManager) {
            fillLinksList()
            if (Settings.hideNotificationIfEmpty && linkList.isEmpty()) {
                // remove notification
                cancelAll()
            } else {
                // set notification
                channelBuilder.ensureChannelsExist(createChannel)
                notify(NOTIFICATION_ID, buildNotification(NORMAL_CHANNEL_ID))
            }
        }
    }

    private fun buildNotification(channelId: String): Notification {
        val intent = buildOnNotificationClickIntent()
        val (title, body, expandToSeeMore) = buildStrings()
        return with(NotificationCompat.Builder(context, channelId)) {
            setContentTitle(title)
            setTicker(title)
            setContentText(expandToSeeMore)
            setStyle(NotificationCompat.BigTextStyle().bigText(body))
            setSmallIcon(R.drawable.ic_notification)
            if (BuildConfig.DEBUG) setShowWhen(true)
            setAutoCancel(false)
            setOngoing(true)
            setNumber(linkList.size)
            setContentIntent(intent)
            setGroup(GROUP_KEY)
            setDefaults(0)
            setOnlyAlertOnce(true)
            build()
        }
    }

    private fun fillLinksList() {
        doAsync {
            linkList.clear()
            linkList.addAll(dao.getAllLinksFromMostRecent().filterAndSortForNotification())
        }.get()
    }

    private fun buildStrings(): Triple<String, String, String> {
        val title = "${linkList.size} link${if (linkList.size != 1) "s" else ""} to be read"
        val expandToSeeMore = if (linkList.isNotEmpty()) "Expand to see the more" else ""
        val body: String =
                if (linkList.isEmpty()) "You're all set!"
                else linkList
                        .map {
                            if (it.title.length >= 50) Pair(it.title.substring(0, 45) + "...", it.timestamp)
                            else Pair(it.title, it.timestamp)
                        }
                        .map {
                            "⌛️ ${it.second.getRemainingTime().toHHmm()} ➡️ ${it.first}\n"
                        }
                        .reduce { acc, s -> "$acc$s" }

        return Triple(title, body, expandToSeeMore)
    }

    private fun buildOnNotificationClickIntent(): PendingIntent {
        val notificationIntent = Intent(context, LinksActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(context, 0, notificationIntent, 0)
    }
}