package com.ghost.bolt.ui.components.card

// Assuming you use Coil for image loading
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.ghost.bolt.models.MediaCardUiModel

@Composable
internal fun MediaListView(
    media: MediaCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {

        Row(modifier = Modifier.fillMaxSize()) {

            // 🔥 Poster
            CoverImage(
                title = media.title,
                mediaId = media.id,
                posterUrl = media.posterUrl,
                sharedScope = sharedScope,
                animatedScope = animatedScope
            )

            // 🔥 Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                // Title
                Text(
                    text = media.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.optionalSharedElement(
                        key = "title_${media.id}",
                        sharedScope = sharedScope,
                        animatedScope = animatedScope
                    )
                )

                // Year + Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    media.releaseDate?.take(4)?.let { year ->
                        Text(
                            text = year,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.optionalSharedElement(
                                key = "release_${media.id}",
                                sharedScope = sharedScope,
                                animatedScope = animatedScope
                            )
                        )
                    }

                    media.voteAverage?.takeIf { it > 0f }?.let { rating ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.optionalSharedElement(
                                key = "rating_${media.id}",
                                sharedScope = sharedScope,
                                animatedScope = animatedScope
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Overview
                Text(
                    text = media.overview ?: "No overview available.",
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
    }
}


@Composable
fun CoverImage(
    title: String,
    mediaId: Int,
    posterUrl: String?,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null,
    clip: Shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
) {

    val imageModifier = modifier
        .fillMaxHeight()
        .aspectRatio(2f / 3f)
        .clip(clip)
        .optionalSharedElement(
            key = "poster_$mediaId",
            sharedScope = sharedScope,
            animatedScope = animatedScope
        )

    SubcomposeAsyncImage(
        model = posterUrl,
        contentDescription = "$title poster",
        contentScale = ContentScale.Crop,
        modifier = imageModifier
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Image")
                }
            }

            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}
