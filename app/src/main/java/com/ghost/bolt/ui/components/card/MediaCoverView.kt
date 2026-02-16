package com.ghost.bolt.ui.components.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun SharedTransitionScope.MediaCoverView(
    mediaId: Int,
    title: String,
    posterUrl: String?,
    variant: CoverVariant,
    modifier: Modifier = Modifier,
    onMediaClick: (Int) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    val posterWidth = when (variant) {
        CoverVariant.NORMAL -> 140.dp
        CoverVariant.MINIMAL -> 120.dp
        CoverVariant.COMPACT -> 110.dp
    }
    // A vertical layout for grid grids
    Column(
        modifier = modifier
            .width(posterWidth)
            .clickable(onClick = { onMediaClick(mediaId) })
    ) {
        CoverImage(
            title,
            mediaId,
            posterUrl,
            animatedVisibilityScope,
            clip = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxHeight(),
        )

        // Show text conditionally based on the variant
        if (variant != CoverVariant.COMPACT) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = if (variant == CoverVariant.MINIMAL) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "title_$mediaId"),
                        animatedVisibilityScope // Pass the explicit scope here
                    )
            )
        }
    }
}