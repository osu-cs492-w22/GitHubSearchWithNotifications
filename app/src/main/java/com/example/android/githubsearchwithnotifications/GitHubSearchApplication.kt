package com.example.android.githubsearchwithnotifications

import android.app.Application
import androidx.work.*
import com.example.android.githubsearchwithnavigation.work.BookmarksSyncWorker
import java.util.concurrent.TimeUnit

class GitHubSearchApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        launchBookmarksSyncWorker()
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
}