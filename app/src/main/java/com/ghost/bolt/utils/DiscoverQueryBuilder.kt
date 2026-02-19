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
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.DiscoverFilter

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

        if (whereConditions.isNotEmpty()) {
            sb.append("WHERE ").append(whereConditions.joinToString(" AND "))
        }

        // ORDER BY clause
//        sb.append(" ORDER BY ")
////        sb.append(filter.sortOrder.name)
//        sb.append(" DESC")

        return SimpleSQLiteQuery(sb.toString(), args.toTypedArray())
    }
}
