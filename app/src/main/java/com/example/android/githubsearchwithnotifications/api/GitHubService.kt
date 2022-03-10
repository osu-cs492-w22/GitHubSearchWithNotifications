package com.example.android.githubsearchwithnotifications.api

import com.example.android.githubsearchwithnotifications.data.GitHubRepo
import com.example.android.githubsearchwithnotifications.data.GitHubSearchResults
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface GitHubService {
    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("sort") sort: String? = "stars"
    ) : GitHubSearchResults

    @GET("repos/{name}")
    suspend fun getRepo(
        @Path("name", encoded = true) name: String
    ) : GitHubRepo

    @POST("repos")
    suspend fun createRepo(
        @Body repo: GitHubRepo
    )

    companion object {
        private const val BASE_URL = "https://api.github.com/"
        fun create() : GitHubService {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(GitHubService::class.java)
        }
    }
}