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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ghost.bolt.enums.AppMediaType

@Composable
fun SharedTransitionScope.MediaDetailedView(
    mediaId: Int,
    title: String,
    posterUrl: String?,
    backdropUrl: String?,
    voteAverage: Float?,
    voteCount: Int?, // Extra info
    overview: String?,
    releaseDate: String?,
    mediaType: AppMediaType?, // Extra info (Movie / TV)
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    // We use an ElevatedCard to give it a slightly more premium, "detailed" feel
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp), // Taller than the standard 140.dp list view
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onMediaClick(mediaId, posterUrl, title, backdropUrl) },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. Larger Poster on the Left
            CoverImage(
                title,
                mediaId,
                posterUrl,
                animatedVisibilityScope,
                clip = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp),
                modifier = Modifier.weight(1f)
            )

            // 2. Richer Metadata on the Right
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title (Allows 2 lines in case of long titles)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "title_$mediaId"),
                        animatedVisibilityScope
                    )
                )

                // Subtitle Row: Media Type & Year
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (mediaType != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = mediaType.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                    .sharedElement(
                                        rememberSharedContentState(key = "media_type_$mediaId"),
                                        animatedVisibilityScope
                                    )
                            )
                        }
                    }

                    if (!releaseDate.isNullOrBlank()) {
                        Text(
                            text = releaseDate.take(4),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "release_date_$mediaId"),
                                animatedVisibilityScope
                            )
                        )
                    }
                }

                // Rating Row with Vote Count
                if (voteAverage != null && voteAverage > 0f) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "rating_$mediaId"),
                            animatedVisibilityScope
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)

                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", voteAverage),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (voteCount != null && voteCount > 0) {
                            Text(
                                text = " ($voteCount votes)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Longer Overview (Allows 3-4 lines instead of 2)
                Text(
                    text = overview?.takeIf { it.isNotBlank() } ?: "No overview available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "overview_$mediaId"),
                        animatedVisibilityScope
                    )
                )
            }
        }
    }
}