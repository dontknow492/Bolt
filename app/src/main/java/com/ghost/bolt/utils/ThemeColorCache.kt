package com.ghost.bolt.utils

import androidx.compose.ui.graphics.Color

object ThemeColorCache {

    private val colorMap = mutableMapOf<String, Color>()

    fun get(url: String): Color? {
        return colorMap[url]
    }

    fun put(url: String, color: Color) {
        colorMap[url] = color
    }

}
