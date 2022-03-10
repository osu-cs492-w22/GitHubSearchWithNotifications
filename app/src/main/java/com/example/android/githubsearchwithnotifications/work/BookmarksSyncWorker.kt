package com.example.android.githubsearchwithnavigation.work

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.githubsearchwithnotifications.R
import com.example.android.githubsearchwithnotifications.api.GitHubService
import com.example.android.githubsearchwithnotifications.data.AppDatabase
import com.example.android.githubsearchwithnotifications.data.BookmarkedReposRepository
import com.example.android.githubsearchwithnotifications.data.GitHubRepo
import com.example.android.githubsearchwithnotifications.data.GitHubReposRepository

class BookmarksSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val TAG = "BookmarksSyncWorker"
    private val STARS_NOTIFICATION_GROUP = "com.example.android.githubsearchwithnotifications.starsNotifications"
    private val SUMMARY_ID = 0
    private val bookmarkedReposRepository = BookmarkedReposRepository(
        AppDatabase.getInstance(context).gitHubRepoDao()
    )
    private val githubReposRepository = GitHubReposRepository(GitHubService.create())

    override suspend fun doWork(): Result {
        val bookmarkedRepos = bookmarkedReposRepository.getAllBookmarkedReposOnce()
        val updatedRepos = mutableListOf<GitHubRepo>()
        for (bookmarkedRepo in bookmarkedRepos) {
            val repoResult = githubReposRepository.loadRepo(bookmarkedRepo.name)
            if (repoResult.isSuccess) {
                val fetchedRepo = repoResult.getOrThrow()
                bookmarkedReposRepository.updateBookmarkedRepo(fetchedRepo)
                Log.d(TAG, "Successfully updated ${bookmarkedRepo.name}")
                if (fetchedRepo.stars >= bookmarkedRepo.stars) {
                    sendNotification(fetchedRepo)
                    updatedRepos.add(fetchedRepo)
                }
            }
        }
        if (updatedRepos.size > 1) sendSummaryNotification(updatedRepos)
        return Result.success()
    }

    private fun sendNotification(repo: GitHubRepo) {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.repo_detail)
            .setArguments(bundleOf(
                "repo" to repo,
                "intVal" to 16
            ))
            .createPendingIntent()

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_stars_channel)
        )
        builder.setSmallIcon(R.drawable.ic_github_logo)
            .setContentTitle(applicationContext.getString(
                R.string.notification_stars_title, repo.name
            ))
            .setContentText(applicationContext.getString(
                R.string.notification_stars_text,
                repo.name,
                repo.stars
            ))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(STARS_NOTIFICATION_GROUP)

        NotificationManagerCompat.from(applicationContext)
            .notify(repo.name.hashCode(), builder.build())
    }

    private fun sendSummaryNotification(repos: List<GitHubRepo>) {
        val pendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.main_nav_graph)
            .setDestination(R.id.bookmarked_repos)
            .createPendingIntent()

        val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_stars_channel)
        )
        builder.setSmallIcon(R.drawable.ic_github_logo)
            .setContentTitle(applicationContext.getString(
                R.string.notification_stars_summary_title, repos.size
            ))
            .setContentText(repos.joinToString(separator = ", ") { it.name })
            .setGroup(STARS_NOTIFICATION_GROUP)
            .setGroupSummary(true)
            .setStyle(NotificationCompat.InboxStyle().also {
                for (repo in repos) {
                    it.addLine(applicationContext.getString(
                        R.string.notification_stars_text, repo.name, repo.stars
                    ))
                }
            })
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(applicationContext)
            .notify(SUMMARY_ID, builder.build())
    }
}