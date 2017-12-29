package com.jaus.albertogiunta.readit.notifications

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context


class NotificationService : JobService() {

    override fun onStartJob(params: JobParameters): Boolean {
        NotificationBuilder.instance.sendBundledNotification()
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    companion object {
        private val JOB_ID = 1
        private val ONE_SEC: Long = 1000
        private val ONE_MIN: Long = 60 * ONE_SEC
        private val FIFTEEN_MIN: Long = 15 * ONE_MIN
        private val TIME: Long = FIFTEEN_MIN
        private var isAlreadyScheduled = false

        fun schedule(context: Context) {
            if (isAlreadyScheduled) return
            val jobScheduler = context.applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val builder = JobInfo
                    .Builder(JOB_ID, ComponentName(context.applicationContext, NotificationService::class.java))
                    .setPersisted(true)
                    .setPeriodic(TIME)
                    .build()
            jobScheduler.schedule(builder)
            isAlreadyScheduled = true
        }
    }
}