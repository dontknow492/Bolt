package com.ghost.bolt.api.response


import com.ghost.bolt.enums.AppMediaType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.time.LocalDate
import java.time.ZoneId

/**
 * Represents a Movie object from the Network.
 * Covers both list-view fields (lighter) and detail-view fields (budget, revenue, etc).
 */
@Serializable
data class TMDbNetworkMedia(
    // Common Fields
    @SerialName("id") val id: Int,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("vote_average") val voteAverage: Double? = null,
    @SerialName("vote_count") val voteCount: Int? = null,
    @SerialName("popularity") val popularity: Double? = null,
    @SerialName("original_language") val originalLanguage: String? = null,
    @SerialName("adult") val adult: Boolean? = null,
    @SerialName("genre_ids") val genreIds: List<Int>? = null,

    // Trending
    @SerialName("media_type") val trendingMediaType: String? = null,

    // Movie Specific
    @SerialName("title") val title: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("original_title") val originalTitle: String? = null,
    @SerialName("video") val video: Boolean? = null,

    // TV Specific (New Fields)
    @SerialName("name") val name: String? = null,
    @SerialName("first_air_date") val firstAirDate: String? = null,
    @SerialName("original_name") val originalName: String? = null,
    @SerialName("origin_country") val originCountry: List<String>? = emptyList(),
    // TV-Specific Status Fields
    @SerialName("last_air_date") val lastAirDate: String? = null,
    @SerialName("in_production") val inProduction: Boolean? = null,
    @SerialName("number_of_seasons") val numberOfSeasons: Int? = null,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int? = null,

    //extra
    @SerialName("genres") val genres: List<NetworkGenre>? = null,
    @SerialName("production_companies") val productionCompanies: List<NetworkProductionCompany>? = null,
    @SerialName("production_countries") val productionCountries: List<NetworkProductionCountry>? = null,
    @SerialName("spoken_languages") val spokenLanguages: List<NetworkSpokenLanguage>? = null,
    @SerialName("keywords") val keywords: NetworkKeywords? = null,

    // Detail & Appended Fields
    @SerialName("budget") val budget: Long? = null,
    @SerialName("revenue") val revenue: Long? = null,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("homepage") val homepage: String? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    @SerialName("credits") val credits: NetworkCredits? = null,
    @SerialName("videos") val videos: NetworkVideos? = null,
    @SerialName("similar") val similar: TmdbResponse? = null,
    @SerialName("recommendations") val recommendations: TmdbResponse? = null,

    //
    @SerialName("belongs_to_collection") val belongsToCollection: String? = null,
//    @SerialName("origin_country") val originCountry: String? = null,
) {
    /**
     * Helper to get a title regardless of media type
     */
    val displayTitle: String
        get() = title ?: name ?: "Unknown Title"

    /**
     * Helper to get a date regardless of media type
     */
    val releaseDateLong: Long?
        get() = try {
            val dateStr = releaseDate ?: firstAirDate // Check both movie and tv date fields
            if (!dateStr.isNullOrBlank()) {
                LocalDate.parse(dateStr)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } else null
        } catch (e: Exception) {
            Timber.e(e, "Error parsing date")
            null
        }

    val lastAirDateLong: Long?
        get() = try {
            if (!lastAirDate.isNullOrBlank()) {
                LocalDate.parse(lastAirDate)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            } else null
        } catch (e: Exception) {
            Timber.e(e, "Error parsing date")
            null
        }

    val mediaType: AppMediaType
        get() {
            // If 'title' exists, it's a Movie.
            // If 'title' is null but 'name' exists, it's a TV show.
            return if (title != null) {
                AppMediaType.MOVIE
            } else {
                AppMediaType.TV
            }
        }
}

// --- Helper Models for Appended Data ---

@Serializable
data class NetworkSpokenLanguage(
    @SerialName("iso_639_1") val isoCode: String, // Note: Maps to your Entity ID
    @SerialName("name") val name: String,
    @SerialName("english_name") val englishName: String

)

