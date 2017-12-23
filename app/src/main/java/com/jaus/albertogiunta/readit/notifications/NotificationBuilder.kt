package com.jaus.albertogiunta.readit.notifications

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.jaus.albertogiunta.readit.R
import com.jaus.albertogiunta.readit.utils.SystemUtils.atLeast
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("JoinDeclarationAndAssignment")
class NotificationBuilder private constructor(ctx: Context) {

    private var context: Context
    private var notificationManager: NotificationManagerCompat
    private var channelBuilder: NotificationChannelBuilder
    private var createChannel: ((channelId: String) -> NotificationChannel?)

    init {
        context = ctx.applicationContext
        notificationManager = NotificationManagerCompat.from(context)
        channelBuilder = NotificationChannelBuilder(context, CHANNEL_IDS)

        @TargetApi(Build.VERSION_CODES.O)
        createChannel = { channelId ->
            when (channelId) {
                NORMAL_CHANNEL_ID -> NotificationChannel(channelId,
                        context.getString(R.string.normal),
                        NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = context.getString(R.string.normal)
                }
                else -> null
            }
        }
    }

    companion object {
        val GROUP_KEY = "Links"
        var NOTIFICATION_ID = 42
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
            channelBuilder.ensureChannelsExist(createChannel)
            notify(NOTIFICATION_ID++, buildNotification(NORMAL_CHANNEL_ID))
        }
    }


    private fun buildNotification(channelId: String): Notification {
        return with(NotificationCompat.Builder(context, channelId)) {
            setContentTitle("ciaone grande")
            setContentText("ciaone piccolo")
            setSmallIcon(R.drawable.ic_copy)
            setShowWhen(true)
            setGroup(GROUP_KEY)
            build()
        }
    }
}