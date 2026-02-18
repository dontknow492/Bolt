package com.ghost.bolt.api.tmdb

import com.ghost.bolt.api.tmdb.response.TMDbMovie
import com.ghost.bolt.api.tmdb.response.TMDbMovieDetailResponse
import com.ghost.bolt.api.tmdb.response.TMDbTv
import com.ghost.bolt.api.tmdb.response.TMDbTvDetailResponse
import com.ghost.bolt.api.tmdb.response.TmdbResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {
    // --- Movie Lists ---
    @GET("movie/{category}")
    suspend fun getMovieList(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): TmdbResponse<TMDbMovie>


    @GET("tv/{category}")
    suspend fun getTvList(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): TmdbResponse<TMDbTv>


    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(
        @Path("time_window") timeWindow: String = "day",
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): TmdbResponse<TMDbMovie>

    @GET("trending/tv/{time_window}")
    suspend fun getTrendingTv(
        @Path("time_window") timeWindow: String = "day",
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): TmdbResponse<TMDbTv>


//    @GET("discover/{media_type}")
//    suspend fun discover(
//        @Path("media_type") mediaType: String, // movie, tv
//        @Query("page") page: Int,
//        @Query("language") language: String = "en-US",
//        @Query("with_genres") genres: String? = null,
//        @Query("sort_by") sortBy: String? = null,
//        @Query("primary_release_year") year: Int? = null
//    ): TmdbResponse


    // --- Details ---
    @GET("movie/{id}")
    suspend fun getMovieDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
        @Query("append_to_response")
        appendToResponse: String = "credits,similar,videos,images"
    ): TMDbMovieDetailResponse


    @GET("tv/{id}")
    suspend fun getTvDetails(
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
        @Query("append_to_response")
        appendToResponse: String = "credits,similar,videos,images"
    ): TMDbTvDetailResponse


}