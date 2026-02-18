package com.ghost.bolt.ui.components.card

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ghost.bolt.models.MediaCardUiModel

@Composable
internal fun MediaCoverView(
    media: MediaCardUiModel,
    variant: CoverVariant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sharedScope: SharedTransitionScope? = null,
    animatedScope: AnimatedVisibilityScope? = null
) {

    val posterWidth = when (variant) {
        CoverVariant.NORMAL -> 140.dp
        CoverVariant.MINIMAL -> 120.dp
        CoverVariant.COMPACT -> 110.dp
    }

    Column(
        modifier = modifier
            .width(posterWidth)
            .clickable(onClick = onClick)
    ) {

        // 🔥 Poster
        CoverImage(
            title = media.title,
            mediaId = media.id,
            posterUrl = media.posterUrl,
            sharedScope = sharedScope,
            animatedScope = animatedScope,
            clip = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
        )

        // 🔥 Title (only for non-compact variants)
        if (variant != CoverVariant.COMPACT) {

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = media.title,
                style = MaterialTheme.typography.labelMedium,
                maxLines = if (variant == CoverVariant.MINIMAL) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .optionalSharedElement(
                        key = "title_${media.id}",
                        sharedScope = sharedScope,
                        animatedScope = animatedScope
                    )
            )
        }
    }
}
