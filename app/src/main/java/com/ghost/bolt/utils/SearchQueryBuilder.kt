package com.ghost.bolt.utils

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.ghost.bolt.enums.SearchFilter
import java.util.Calendar

object SearchQueryBuilder {

    fun build(filter: SearchFilter): SupportSQLiteQuery {
        val queryBuilder =
            StringBuilder("SELECT * FROM Media WHERE 1=1 ") // 1=1 makes appending "AND" easier
        val args = ArrayList<Any>()

        // 1. Text Search
        if (filter.query.isNotBlank()) {
            queryBuilder.append("AND title LIKE '%' || ? || '%' ")
            args.add(filter.query)
        }

        // 2. Media Type
        if (filter.mediaType != null) {
            queryBuilder.append("AND media_type = ? ")
            args.add(filter.mediaType.name)
        }

        // 3. Vote Average

        return SimpleSQLiteQuery(queryBuilder.toString(), args.toArray())
    }

    private fun getTimestampForYear(year: Int): Long {
        // Simple helper to get Jan 1st of that year
        val calendar = Calendar.getInstance()
        calendar.set(year, 0, 1, 0, 0, 0)
        return calendar.timeInMillis
    }
}