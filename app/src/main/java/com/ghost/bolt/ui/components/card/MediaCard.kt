package com.ghost.bolt.ui.components.card

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghost.bolt.enums.AppMediaType

@Composable
fun SharedTransitionScope.MediaCard(
    modifier: Modifier = Modifier,
    mediaId: Int,
    title: String,
    posterUrl: String?,
    voteAverage: Float?,
    voteCount: Int? = null, // New
    overview: String?,
    releaseDate: String?,
    mediaType: AppMediaType? = null, // New
    style: MediaCardStyle,
    onMediaClick: (Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    // AnimatedContent handles the state transition automatically
    AnimatedContent(
        targetState = style,
        transitionSpec = {
            // A smooth crossfade transition
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        },
        label = "MediaCardTransition"
    ) { targetStyle ->
        when (targetStyle) {
            is MediaCardStyle.List -> {
                MediaListView(
                    mediaId = mediaId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    title = title,
                    posterUrl = posterUrl,
                    voteAverage = voteAverage,
                    overview = overview,
                    onMediaClick = onMediaClick,
                    releaseDate = releaseDate,
                    modifier = modifier
                )
            }
            is MediaCardStyle.Cover -> {
                // Passes the specific variant (Compact, Minimal, etc.) down
                MediaCoverView(
                    mediaId = mediaId,
                    title = title,
                    posterUrl = posterUrl,
                    variant = targetStyle.variant,
                    modifier = modifier,
                    onMediaClick = onMediaClick,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
            is MediaCardStyle.Detailed -> {
                // Call the new Detailed view here!
                MediaDetailedView(
                    mediaId = mediaId,
                    animatedVisibilityScope = animatedVisibilityScope,
                    title = title,
                    posterUrl = posterUrl,
                    voteAverage = voteAverage,
                    voteCount = voteCount,
                    overview = overview,
                    releaseDate = releaseDate,
                    mediaType = mediaType,
                    onMediaClick = onMediaClick,
                    modifier = modifier
                )
            }
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
private fun MediaCardPreview() {
    // Sample data
    val sampleMediaId = 1
    val sampleTitle = "Inception"
    val samplePosterUrl = "https://image.tmdb.org/t/p/w500/sample_image.jpg"
    val sampleVoteAverage = 8.8f
    val sampleVoteCount = 2000
    val sampleOverview = "A thief who steals corporate secrets through the use of dream-sharing technology."
    val sampleReleaseDate = "2010-07-16"
    val sampleMediaType = AppMediaType.MOVIE
    val sampleStyle = MediaCardStyle.Detailed

    val styles = listOf(
        MediaCardStyle.Detailed,
        MediaCardStyle.List,
        MediaCardStyle.Cover(CoverVariant.NORMAL),
        MediaCardStyle.Cover(CoverVariant.MINIMAL),
        MediaCardStyle.Cover(CoverVariant.COMPACT)
    )

    // 1. Provide the SharedTransitionScope
    SharedTransitionLayout {

        // 2. Artificially provide the AnimatedVisibilityScope
        AnimatedVisibility(visible = true) {

            // 3. Now 'this' is the AnimatedVisibilityScope you need!
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                styles.forEach { style ->
                    MediaCard(
                        mediaId = sampleMediaId,
                        title = sampleTitle,
                        onMediaClick = {},
                        posterUrl = samplePosterUrl,
                        voteAverage = sampleVoteAverage,
                        voteCount = sampleVoteCount,
                        overview = sampleOverview,
                        releaseDate = sampleReleaseDate,
                        mediaType = sampleMediaType,
                        style = style,
                        animatedVisibilityScope = this@AnimatedVisibility // Pass the scope here
                    )
                    HorizontalDivider(modifier = Modifier.width(2.dp))
                }
            }

        }
    }
}