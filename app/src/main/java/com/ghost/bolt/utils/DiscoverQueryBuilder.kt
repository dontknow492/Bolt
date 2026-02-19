/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghost.bolt.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import com.ghost.bolt.enums.DiscoverFilter
import com.ghost.bolt.enums.LogicMode

object DiscoverQueryBuilder {

    fun build(filter: DiscoverFilter): SimpleSQLiteQuery {
        val sb = StringBuilder()
        val args = mutableListOf<Any>()

        sb.append("SELECT * FROM Media ")

        // WHERE clause
        val whereConditions = mutableListOf<String>()
        filter.mediaType?.let {
            whereConditions.add("media_type = ?")
            args.add(it.name)
        }

        // 4. Year Range (Stored as Unix Timestamp in MediaEntity)
        // Note: You'll need a helper to convert Year to Timestamp (approx Jan 1st)
        filter.minYear?.let {
            sb.append(" AND Media.release_date >= ?")
            args.add(getYearTimestamp(it))
        }
        filter.maxYear?.let {
            sb.append(" AND Media.release_date <= ?")
            args.add(getYearTimestamp(it, isEnd = true))
        }

        // 5. Vote Average
        filter.minVote?.let {
            sb.append(" AND Media.vote_average >= ?")
            args.add(it)
        }

        // 6. Runtime (Movies usually)
        filter.minRuntime?.let {
            sb.append(" AND Media.runtime >= ?")
            args.add(it)
        }
        filter.maxRuntime?.let {
            sb.append(" AND Media.runtime <= ?")
            args.add(it)
        }

        if (filter.genres.isNotEmpty()) {
            val placeholder = filter.genres.joinToString(",") { "?" }
            if (filter.genreLogic == LogicMode.AND) {
                // For 'AND', the movie must have ALL selected genres
                sb.append(" AND Media.id IN (SELECT media_id FROM MediaGenres WHERE genre_id IN ($placeholder) GROUP BY media_id HAVING COUNT(genre_id) = ?)")
                filter.genres.forEach { args.add(it) }
                args.add(filter.genres.size)
            } else {
                // For 'OR', the movie just needs one of them
                sb.append(" AND MediaGenres.genre_id IN ($placeholder)")
                filter.genres.forEach { args.add(it) }
            }
        }


        // 7. Sort Order
        // Always use the position from the Mediator to respect the API's sorting
//        sb.append(" ORDER BY MediaCategories.position_in_category ASC")

        if (whereConditions.isNotEmpty()) {
            sb.append("WHERE ").append(whereConditions.joinToString(" AND "))
        }

        // ORDER BY clause
        sb.append(" ORDER BY ")
        sb.append(filter.sortOption.sql)
        sb.append(" DESC")

        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())

    }

    private fun getYearTimestamp(year: Int, isEnd: Boolean = false): Long {
        val calendar = java.util.Calendar.getInstance()
        if (isEnd) calendar.set(year, 11, 31, 23, 59)
        else calendar.set(year, 0, 1, 0, 0)
        return calendar.timeInMillis
    }
}
