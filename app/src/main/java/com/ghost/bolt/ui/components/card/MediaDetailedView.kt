package com.ghost.bolt.ui.components.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.models.MediaCardUiModel
import java.util.Locale

/* ------------------------------------------------ */
/* ---------------- UI MODEL ---------------------- */
/* ------------------------------------------------ */

data class MediaDetailedUiModel(
    val id: Int,
    val title: String,
    val posterUrl: String?,
    val backdropUrl: String?,
    val voteAverage: Float?,
    val voteCount: Int?,
    val overview: String?,
    val releaseDate: String?,
    val mediaType: AppMediaType,
    val mediaSource: MediaSource
)

/* ------------------------------------------------ */
/* ---------------- MAIN CARD --------------------- */
/* ------------------------------------------------ */

@Composable
internal fun MediaDetailedView(
    media: MediaCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
) {

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onClick
    ) {

        Row(Modifier.fillMaxSize()) {

            // 🔥 Poster
            CoverImage(
                title = media.title,
                mediaId = media.id,
                posterUrl = media.posterUrl,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                clip = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                modifier = Modifier.weight(1f)
            )

            // 🔥 Content
            MediaDetailsContent(
                media = media,
                sharedScope = sharedScope,
                animatedScope = animatedScope,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
    }
}

/* ------------------------------------------------ */
/* ---------------- CONTENT ----------------------- */
/* ------------------------------------------------ */

@Composable
private fun MediaDetailsContent(
    media: MediaCardUiModel,
    modifier: Modifier,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?
) {

    val year = remember(media.releaseDate) {
        media.releaseDate?.take(4)
    }

    val ratingFormatted = remember(media.voteAverage) {
        media.voteAverage
            ?.takeIf { it > 0f }
            ?.let { String.format(Locale.US, "%.1f", it) }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {

        // 🔥 Title
        Text(
            text = media.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.optionalSharedElement(
                key = "title_${media.id}",
                sharedScope = sharedScope,
                animatedScope = animatedScope
            )
        )

        // 🔥 Type + Year
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            MediaTypeChip(
                media = media,
                sharedScope = sharedScope,
                animatedScope = animatedScope
            )

            year?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.optionalSharedElement(
                        key = "release_${media.id}",
                        sharedScope = sharedScope,
                        animatedScope = animatedScope
                    )
                )
            }
        }

        // 🔥 Rating
        ratingFormatted?.let {
            RatingRow(
                rating = it,
                voteCount = media.voteCount,
                modifier = Modifier.optionalSharedElement(
                    key = "rating_${media.id}",
                    sharedScope = sharedScope,
                    animatedScope = animatedScope
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // 🔥 Overview
        Text(
            text = media.overview?.takeIf { it.isNotBlank() }
                ?: "No overview available.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.optionalSharedElement(
                key = "overview_${media.id}",
                sharedScope = sharedScope,
                animatedScope = animatedScope
            )
        )
    }
}


/* ------------------------------------------------ */
/* ---------------- SMALL COMPONENTS -------------- */
/* ------------------------------------------------ */

@Composable
private fun MediaTypeChip(
    media: MediaCardUiModel,
    sharedScope: SharedTransitionScope?,
    animatedScope: AnimatedVisibilityScope?
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = media.mediaType.name,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier
                .padding(horizontal = 6.dp, vertical = 2.dp)
                .optionalSharedElement(
                    key = "type_${media.id}",
                    sharedScope = sharedScope,
                    animatedScope = animatedScope
                )
        )
    }
}


@Composable
private fun RatingRow(
    rating: String,
    voteCount: Int?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = rating,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )

        voteCount?.takeIf { it > 0 }?.let {
            Text(
                text = " ($it votes)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

/* ------------------------------------------------ */
/* ----------- SHARED ELEMENT HELPER ------------- */
/* ------------------------------------------------ */

@Composable
private fun SharedTransitionScope.shared(
    mediaId: Int,
    key: String,
    scope: AnimatedVisibilityScope
) = Modifier.sharedElement(
    rememberSharedContentState("${key}_$mediaId"),
    scope
)
