package com.ghost.bolt.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.ghost.bolt.ui.components.loadImageBitmapFromUrl
import com.ghost.bolt.ui.theme.SeedColor
import com.materialkolor.ktx.themeColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThemeColorCache {

    private val colorMap = mutableMapOf<String, Color>()

    fun get(url: String): Color? {
        return colorMap[url]
    }

    fun put(url: String, color: Color) {
        colorMap[url] = color
    }

}
