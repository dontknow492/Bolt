package com.ghost.bolt.database

import androidx.room.TypeConverter
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.RefreshFrequency
import com.ghost.bolt.enums.Season
import com.ghost.bolt.enums.Status
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Converters {

    // A formatter to match the Kaggle/TMDB SQL format (YYYY-MM-DD)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * Reads the "YYYY-MM-DD" String from your SQLite database
     * and turns it into a Long (Unix Timestamp) for Jetpack Compose.
     */
    @TypeConverter
    fun fromSqlDateStringToLong(dateString: String?): Long? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            dateFormat.parse(dateString)?.time // Returns the time in milliseconds (Long)
        } catch (e: Exception) {
            null // Fallback if the data is messy or malformed
        }
    }

    /**
     * Takes the Long from your Kotlin app and turns it back into
     * a "YYYY-MM-DD" String to save in the SQLite database.
     */
    @TypeConverter
    fun fromLongToSqlDateString(timestamp: Long?): String? {
        if (timestamp == null) return null
        return dateFormat.format(Date(timestamp))
    }

    @TypeConverter
    fun fromLongToSqlDate(timestamp: Long?): Date? {
        if (timestamp == null) return null
        return Date(timestamp)
    }

    @TypeConverter
    fun fromSqlDateToLong(date: Date?): Long? {
        if (date == null) return null
        return date.time
    }


    // --- MediaType Converters ---
    @TypeConverter
    fun fromMediaTypeString(value: String?): AppMediaType? {
        if (value == null) return null
        return AppMediaType.entries.find { it.name.equals(value, ignoreCase = true) }
    }

    @TypeConverter
    fun mediaTypeToString(type: AppMediaType?): String? {
        return type?.name
    }

    // --- Status Converters ---
    @TypeConverter
    fun fromStatusString(value: String?): Status? {
        if (value == null) return null
        return Status.entries.find { it.name.equals(value, ignoreCase = true) }
    }

    @TypeConverter
    fun statusToString(status: Status?): String? {
        return status?.name
    }

    // --- Season Converters ---
    @TypeConverter
    fun fromSeasonString(value: String?): Season? {
        if (value == null) return null
        return Season.entries.find { it.name.equals(value, ignoreCase = true) }
    }

    @TypeConverter
    fun seasonToString(season: Season?): String? {
        return season?.name
    }

    // --- RefreshFrequency Converters ---
    @TypeConverter
    fun fromRefreshFrequencyString(value: String?): RefreshFrequency? {
        if (value == null) return null
        return RefreshFrequency.entries.find { it.name.equals(value, ignoreCase = true) }
    }

    @TypeConverter
    fun refreshFrequencyToString(frequency: RefreshFrequency?): String? {
        return frequency?.name
    }
}