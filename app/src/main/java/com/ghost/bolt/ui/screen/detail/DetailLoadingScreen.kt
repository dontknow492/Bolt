package com.ghost.bolt.ui.screen.detail


import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import com.commit451.coiltransformations.BlurTransformation
import com.ghost.bolt.R
import com.ghost.bolt.utils.shimmerEffect

//import com.ghost.bolt.ui.utils.shimmerEffect

sealed class BackdropSource {
    data class Network(val uri: Uri) : BackdropSource()
    data class Local(@param:DrawableRes val resId: Int) : BackdropSource()
}


@Composable
fun DetailLoadingScreen(
    modifier: Modifier = Modifier,
    backdropPath: BackdropSource? = null // Optional: Pass cached image path for instant blur
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = modifier.fillMaxSize()) {

            // 1. Blurred Backdrop / Background
            if (backdropPath != null) {
                val model = when (backdropPath) {
                    is BackdropSource.Local -> backdropPath.resId
                    is BackdropSource.Network -> backdropPath.uri
                }
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(model)
                        // BLUR TRANSFORMATION: Makes the image look like a high-quality background
                        .transformations(
                            BlurTransformation(
                                LocalContext.current,
                                radius = 1f,
                                sampling = 1f
                            )
                        )
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Dark Scrim (Overlay) to make text/shimmer readable
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                )
            } else {
                // Fallback Gradient if no image provided
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )
            }

            DetailScreenShimmer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .align(Alignment.BottomCenter)
            )


            // 3. Linear Progress Indicator (At the very top)
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Ensures it sits below status bar
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )

        }
    }
}

@Composable
private fun DetailScreenShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background.copy(alpha = 0f)
                    ),
                    startY = 0f,
                    endY = 100f
                )
            )
            .padding(16.dp)
    ) {
        // Title Skeleton
        Box(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth(0.7f)
                .clip(RoundedCornerShape(8.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Metadata Row Skeleton (Rating, Year, Time)
        Row {
            Box(
                Modifier
                    .size(60.dp, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                Modifier
                    .size(40.dp, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                Modifier
                    .size(50.dp, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Genres Chips Skeleton
        Row {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .size(80.dp, 32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmerEffect()
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Overview Skeleton (3 lines)
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (it == 2) 0.5f else 1f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Cast Row Skeleton
        Box(
            modifier = Modifier
                .size(100.dp, 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        ) // "Top Cast" title
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(6) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .size(60.dp, 12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DetailLoadingScreenPreview() {
    DetailLoadingScreen(
        modifier = Modifier.fillMaxSize(),
        BackdropSource.Local(R.drawable.error_image_placeholder)
    )
}

