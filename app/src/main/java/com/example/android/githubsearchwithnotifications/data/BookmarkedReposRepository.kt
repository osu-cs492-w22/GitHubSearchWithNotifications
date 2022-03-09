package com.example.android.githubsearchwithnotifications.data

class BookmarkedReposRepository(
    private val dao: GitHubRepoDao
) {
    suspend fun insertBookmarkedRepo(repo: GitHubRepo) = dao.insert(repo)
    suspend fun removeBookmarkedRepo(repo: GitHubRepo) = dao.delete(repo)
    suspend fun updateBookmarkedRepo(repo: GitHubRepo) = dao.update(repo)
    fun getAllBookmarkedRepos() = dao.getAllRepos()
    suspend fun getAllBookmarkedReposOnce() = dao.getAllReposOnce()
    fun getBookmarkedRepoByName(name: String) = dao.getRepoByName(name)
}