package com.ghost.bolt.ui.components.card

// Assuming you use Coil for image loading
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import com.ghost.bolt.R

@Composable
fun SharedTransitionScope.MediaListView(
    mediaId: Int,
    title: String,
    posterUrl: String?,
    voteAverage: Float?,
    overview: String?,
    releaseDate: String?,
    modifier: Modifier = Modifier,
    onMediaClick: (Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp), // Fixed height for consistent list items
        shape = RoundedCornerShape(12.dp),
        onClick = { onMediaClick(mediaId) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. Poster Image (Left Side)
            CoverImage(title, mediaId, posterUrl, animatedVisibilityScope, modifier = Modifier)

            // 2. Text Content (Right Side)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedElement(
                        rememberSharedContentState(key = "title_$mediaId"),
                        animatedVisibilityScope
                    )
                )

                // Date & Rating Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!releaseDate.isNullOrBlank()) {
                        Text(
                            text = releaseDate.take(4), // Just show the Year
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.sharedElement(
                                rememberSharedContentState(key = "release_date_$mediaId"),
                                animatedVisibilityScope
                            )
                        )
                    }

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
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = String.format("%.1f", voteAverage),
                                style = MaterialTheme.typography.labelMedium,

                                )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Overview
                Text(
                    text = overview ?: "No overview available.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 5, // Keeps the card height predictable
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

@Composable
fun SharedTransitionScope.CoverImage(
    title: String,
    mediaId: Int,
    posterUrl: String?,
    animatedVisibilityScope: AnimatedVisibilityScope,
    clip: Shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp),
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = posterUrl, // You'll prepend the TMDB base URL here usually
        contentDescription = "$title poster",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(2f / 3f) // Standard movie poster ratio
            .clip(clip)
            .sharedElement(
                rememberSharedContentState(key = "poster_$mediaId"),
                animatedVisibilityScope // Pass the explicit scope here
            )
    ) {
        val state by painter.state.collectAsState()
        when (state) {
            is AsyncImagePainter.State.Success -> {
                SubcomposeAsyncImageContent()
            }

            is AsyncImagePainter.State.Error -> {
                SubcomposeAsyncImageContent(
                    painter = painterResource(R.drawable.error_image_placeholder),
                    contentDescription = "Error loading poster",
                    contentScale = ContentScale.FillBounds
                )
            }

            is AsyncImagePainter.State.Empty -> {
                Text(text = "Empty")
            }

            else -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}