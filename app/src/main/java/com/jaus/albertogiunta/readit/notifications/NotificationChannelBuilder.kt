package com.jaus.albertogiunta.readit.notifications

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.jaus.albertogiunta.readit.utils.Utils.atLeast

class NotificationChannelBuilder(
        context: Context,
        private val channelIds: List<String>,
        private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
) {

    fun ensureChannelsExist(createChannel: (channelId: String) -> NotificationChannel?) = notificationManager.ensureChannelsExist(createChannel)

    @TargetApi(Build.VERSION_CODES.O)
    private fun NotificationManager.ensureChannelsExist(createChannel: (channelId: String) -> NotificationChannel?) {
        if (atLeast(Build.VERSION_CODES.O)) {
            channelIds
                    .filter { !notificationChannelIds().contains(it) }
                    .forEach {
                        createChannel(it)?.also {
                            notificationManager.createNotificationChannel(it)
                        }
                    }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun NotificationManager.notificationChannelIds() = notificationChannels.map { it.id }

}