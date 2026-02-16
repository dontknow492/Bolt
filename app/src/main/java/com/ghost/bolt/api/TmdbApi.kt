package com.ghost.bolt.api

import com.ghost.bolt.api.response.TMDbNetworkMedia
import com.ghost.bolt.api.response.TmdbResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TmdbApi {
    // --- Movie Lists ---
    @GET("{media_type}/{category}")
    suspend fun getMediaList(
        @Path("media_type") mediaType: String, // movie, tv
        @Path("category") category: String,    // popular, top_rated, upcoming, now_playing, airing_today
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("region") region: String? = null
    ): TmdbResponse

    @GET("trending/{media_type}/{time_window}")
    suspend fun getTrending(
        @Path("media_type") mediaType: String, // all, movie, tv, person
        @Path("time_window") timeWindow: String = "day", // day, week
        @Query("page") page: Int,
        @Query("language") language: String = "en-US"
    ): TmdbResponse


    @GET("discover/{media_type}")
    suspend fun discover(
        @Path("media_type") mediaType: String, // movie, tv
        @Query("page") page: Int,
        @Query("language") language: String = "en-US",
        @Query("with_genres") genres: String? = null,
        @Query("sort_by") sortBy: String? = null,
        @Query("primary_release_year") year: Int? = null
    ): TmdbResponse


    // --- Details ---
    @GET("{media_type}/{id}")
    suspend fun getDetails(
        @Path("media_type") mediaType: String, // movie, tv
        @Path("id") id: Int,
        @Query("language") language: String = "en-US",
        @Query("append_to_response")
        appendToResponse: String = "credits,similar,videos,images"
    ): TMDbNetworkMedia

    @GET("movie/{movie_id}")
    suspend fun getMovie(
        @Path("movie_id") movieId: Int,
        @Query("language") language: String = "en-US",
        @Query("append_to_response") appendToResponse: String = "credits,similar,videos,images"
    ): TMDbNetworkMedia // This returns the full detail object
}