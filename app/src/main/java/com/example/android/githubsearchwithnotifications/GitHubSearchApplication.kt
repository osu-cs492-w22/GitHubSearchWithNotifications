package com.example.android.githubsearchwithnotifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.android.githubsearchwithnavigation.work.BookmarksSyncWorker
import java.util.concurrent.TimeUnit

class GitHubSearchApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        launchBookmarksSyncWorker()
        createStarsNotificationChannel()
    }

    private fun launchBookmarksSyncWorker() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//        val workRequest = PeriodicWorkRequestBuilder<BookmarksSyncWorker>(
//            12,
//            TimeUnit.HOURS
//        ).setConstraints(constraints).build()
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
//            "bookmarksSyncWorker",
//            ExistingPeriodicWorkPolicy.KEEP,
//            workRequest
//        )
        val workRequest = OneTimeWorkRequestBuilder<BookmarksSyncWorker>().build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }

    private fun createStarsNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.notification_stars_channel),
                getString(R.string.notification_stars_channel_title),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}