package com.example.android.githubsearchwithnavigation.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.android.githubsearchwithnotifications.api.GitHubService
import com.example.android.githubsearchwithnotifications.data.AppDatabase
import com.example.android.githubsearchwithnotifications.data.BookmarkedReposRepository
import com.example.android.githubsearchwithnotifications.data.GitHubReposRepository

class BookmarksSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    private val TAG = "BookmarksSyncWorker"
    private val bookmarkedReposRepository = BookmarkedReposRepository(
        AppDatabase.getInstance(context).gitHubRepoDao()
    )
    private val githubReposRepository = GitHubReposRepository(GitHubService.create())

    override suspend fun doWork(): Result {
        val bookmarkedRepos = bookmarkedReposRepository.getAllBookmarkedReposOnce()
        for (bookmarkedRepo in bookmarkedRepos) {
            val repoResult = githubReposRepository.loadRepo(bookmarkedRepo.name)
            if (repoResult.isSuccess) {
                bookmarkedReposRepository.updateBookmarkedRepo(repoResult.getOrThrow())
                Log.d(TAG, "Successfully updated ${bookmarkedRepo.name}")
            }
        }
        return Result.success()
    }
}