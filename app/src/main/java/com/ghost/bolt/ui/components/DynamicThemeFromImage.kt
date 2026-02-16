package com.ghost.bolt.ui.components

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.ImageLoader
import coil3.compose.rememberAsyncImagePainter
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Size
import coil3.toBitmap
import com.ghost.bolt.ui.theme.SeedColor
import com.ghost.bolt.ui.viewModel.DynamicThemeViewModel
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.ktx.rememberThemeColor
import com.materialkolor.quantize.QuantizerCelebi
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.ktx.themeColor
import com.materialkolor.ktx.themeColors
import com.materialkolor.rememberDynamicMaterialThemeState
import com.materialkolor.score.Score
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber



/**
 * The final, robust composable for applying a dynamic theme from an image.
 * It uses the MaterialKolor library for simple and accurate theme generation.
 */
@Composable
fun DynamicThemeFromImage(
    imageUrl: String?,
    viewModel: DynamicThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val seedColor by viewModel.seedColor.collectAsStateWithLifecycle()

    LaunchedEffect(imageUrl) {
        if (imageUrl == null) return@LaunchedEffect
        viewModel.loadThemeColor(imageUrl, context)
    }

    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isSystemInDarkTheme(),
        seedColor = seedColor ?: SeedColor,
        style = PaletteStyle.TonalSpot,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
    )

    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true
    ) {
        content()
    }
}





/**
 * A helper function to load an image with Coil
 *
 * @return ImageBitmap
 */

suspend fun loadImageBitmapFromUrl(
    context: Context,
    imageUrl: Any,
): ImageBitmap? {
    return try {
        val loader = ImageLoader(context)

        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .size(12)
            .allowHardware(true) // IMPORTANT
            .build()

        val result = loader.execute(request)

        if (result is SuccessResult) {
            val bitmap = result.image.toBitmap()
            bitmap.asImageBitmap()
        } else {
            null
        }
    }catch (e: Exception) {
        e.printStackTrace()
        null
    }

}


@Composable
fun DynamicThemeFromImages(
    imageUrl: String?,
    content: @Composable () -> Unit
) {
    var seedColor by remember(imageUrl) { mutableStateOf(SeedColor) }

    val painter = rememberAsyncImagePainter(
        model = imageUrl,
        onSuccess = { result ->
            val bitmap = result.result.image.toBitmap()
            seedColor = bitmap.asImageBitmap()
                .themeColor(fallback = SeedColor)

            Timber.d("New seed extracted: $seedColor")
        }
    )

    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isSystemInDarkTheme(),
        style = PaletteStyle.TonalSpot,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        seedColor = seedColor,
    )

    DynamicMaterialTheme(
        state = dynamicThemeState,
        animate = true,
    ) {
        content()
    }
}