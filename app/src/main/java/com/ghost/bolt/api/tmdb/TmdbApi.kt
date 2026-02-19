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


    // Search
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String? = null,
        @Query("region") region: String? = null,
        @Query("year") year: Int? = null
    ): TmdbResponse<TMDbMovie>


    @GET("search/tv")
    suspend fun searchTv(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        @Query("language") language: String? = null,
        @Query("first_air_date_year") firstAirDateYear: Int? = null
    ): TmdbResponse<TMDbTv>

    //discover
    @GET("discover/movie")
    suspend fun discoverMovie(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,

        // Genres
        @Query("with_genres") withGenres: String? = null,
        @Query("without_genres") withoutGenres: String? = null,

        // Cast & Crew
        @Query("with_cast") withCast: String? = null,
        @Query("with_crew") withCrew: String? = null,

        // Keywords
        @Query("with_keywords") withKeywords: String? = null,

        // Rating
        @Query("vote_average.gte") voteAverageGte: Float? = null,
        @Query("vote_average.lte") voteAverageLte: Float? = null,
        @Query("vote_count.gte") voteCountGte: Int? = null,

        // Release Date Range
        @Query("primary_release_date.gte") releaseDateGte: String? = null,
        @Query("primary_release_date.lte") releaseDateLte: String? = null,

        // Runtime
        @Query("with_runtime.gte") runtimeGte: Int? = null,
        @Query("with_runtime.lte") runtimeLte: Int? = null,

        // Providers (Optional)
        @Query("with_watch_providers") watchProviders: String? = null,
        @Query("watch_region") watchRegion: String? = null,

        // Sorting
        @Query("sort_by") sortBy: String? = "popularity.desc"
    ): TmdbResponse<TMDbMovie>


    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "en-US",
        @Query("include_adult") includeAdult: Boolean = false,

        // Genres
        @Query("with_genres") withGenres: String? = null,
        @Query("without_genres") withoutGenres: String? = null,

        // Cast & Crew
        @Query("with_cast") withCast: String? = null,
        @Query("with_crew") withCrew: String? = null,

        // Keywords
        @Query("with_keywords") withKeywords: String? = null,

        // Rating
        @Query("vote_average.gte") voteAverageGte: Float? = null,
        @Query("vote_average.lte") voteAverageLte: Float? = null,
        @Query("vote_count.gte") voteCountGte: Int? = null,

        // First Air Date Range
        @Query("first_air_date.gte") firstAirDateGte: String? = null,
        @Query("first_air_date.lte") firstAirDateLte: String? = null,

        // Networks
        @Query("with_networks") withNetworks: String? = null,

        // Sorting
        @Query("sort_by") sortBy: String? = "popularity.desc"
    ): TmdbResponse<TMDbTv>


}