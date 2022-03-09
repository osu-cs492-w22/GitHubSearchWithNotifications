package com.example.android.githubsearchwithnotifications.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.githubsearchwithnotifications.data.AppDatabase
import com.example.android.githubsearchwithnotifications.data.BookmarkedReposRepository
import com.example.android.githubsearchwithnotifications.data.GitHubRepo
import kotlinx.coroutines.launch

class BookmarkedReposViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookmarkedReposRepository(
        AppDatabase.getInstance(application).gitHubRepoDao()
    )

    val bookmarkedRepos = repository.getAllBookmarkedRepos().asLiveData()

    fun addBookmarkedRepo(repo: GitHubRepo) {
        viewModelScope.launch {
            repository.insertBookmarkedRepo(repo)
        }
    }

    fun removeBookmarkedRepo(repo: GitHubRepo) {
        viewModelScope.launch {
            repository.removeBookmarkedRepo(repo)
        }
    }

    fun getBookmarkedRepoByName(name: String) =
        repository.getBookmarkedRepoByName(name).asLiveData()
}