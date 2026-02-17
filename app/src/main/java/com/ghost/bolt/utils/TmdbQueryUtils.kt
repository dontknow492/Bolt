package com.ghost.bolt.utils

import com.ghost.bolt.enums.AppendToResponseItem

/**
 * Helper object to build the comma-separated string for "append_to_response"
 * Usage: TmdbQueryUtils.buildAppendToResponse(AppendToResponseItem.CREDITS, AppendToResponseItem.VIDEOS)
 */
object TmdbQueryUtils {
    fun buildAppendToResponse(vararg items: AppendToResponseItem): String {
        return items.joinToString(",") { it.value }
    }
}


