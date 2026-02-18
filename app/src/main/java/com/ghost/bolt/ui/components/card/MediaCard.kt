package com.ghost.bolt.ui.components.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.utils.mapper.toMediaCardUiModel
import timber.log.Timber


@Composable
fun Modifier.optionalSharedElement(
    key: String,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?
): Modifier {
    return if (sharedScope != null && animatedScope != null) {
        with(sharedScope) {
            this@optionalSharedElement.sharedElement(
                rememberSharedContentState(key),
                animatedScope
            )
        }
    } else {
        this
    }
}


@Composable
fun MediaEntityCard(
    entity: MediaEntity,
    mediaStyle: MediaCardStyle,
    modifier: Modifier = Modifier,
    onMediaClick: (MediaCardUiModel) -> Unit,
    animatedContentScope: AnimatedVisibilityScope? = null,
    sharedTransitionScope: SharedTransitionScope? = null
) {
    Timber.tag("Home Screen").v("Rendering entity: ${entity.title}")
    val uiEntity = entity.toMediaCardUiModel()
    MediaCard(
        modifier = modifier,
        media = uiEntity,
        style = mediaStyle,
        onClick = { onMediaClick(uiEntity) },
        sharedTransitionScope = sharedTransitionScope,
        animatedVisibilityScope = animatedContentScope
    )
}


@Composable
fun MediaCard(
    media: MediaCardUiModel,
    style: MediaCardStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
) {

    AnimatedContent(
        targetState = style,
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        label = "MediaCardTransition"
    ) { targetStyle ->

        when (targetStyle) {

            MediaCardStyle.List -> {
                MediaListView(
                    media = media,
                    onClick = onClick,
                    modifier = modifier,
                    sharedScope = sharedTransitionScope,
                    animatedScope = animatedVisibilityScope
                )
            }

            MediaCardStyle.Detailed -> {
                MediaDetailedView(
                    media = media,
                    onClick = onClick,
                    modifier = modifier,
                    sharedScope = sharedTransitionScope,
                    animatedScope = animatedVisibilityScope
                )
            }

            is MediaCardStyle.Cover -> {
                MediaCoverView(
                    media = media,
                    variant = targetStyle.variant,
                    onClick = onClick,
                    modifier = modifier,
                    sharedScope = sharedTransitionScope,
                    animatedScope = animatedVisibilityScope
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
    "Inception"
    "https://image.tmdb.org/t/p/w500/sample_image.jpg"
    "A thief who steals corporate secrets through the use of dream-sharing technology."
    "2010-07-16"
    AppMediaType.MOVIE
    MediaCardStyle.Detailed

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
//                    MediaCard(
//                        mediaId = sampleMediaId,
//                        title = sampleTitle,
//                        onMediaClick = { _, _, _, _, _, _ -> },
//                        posterUrl = samplePosterUrl,
//                        backdropUrl = samplePosterUrl,
//                        voteAverage = sampleVoteAverage,
//                        voteCount = sampleVoteCount,
//                        overview = sampleOverview,
//                        releaseDate = sampleReleaseDate,
//                        mediaType = sampleMediaType,
//                        style = style,
//                        animatedVisibilityScope = this@AnimatedVisibility // Pass the scope here
//                    )
                    HorizontalDivider(modifier = Modifier.width(2.dp))
                }
            }

        }
    }
}