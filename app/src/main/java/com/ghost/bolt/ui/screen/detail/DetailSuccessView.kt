package com.ghost.bolt.ui.screen.detail


import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ghost.bolt.R
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.KeywordEntity
import com.ghost.bolt.database.entity.MediaDetail
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.models.MediaCardUiModel
import com.ghost.bolt.models.UiMediaDetail
import com.ghost.bolt.models.UiRelatedMedia
import com.ghost.bolt.ui.components.CastSection
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.components.card.MediaEntityCard
import com.ghost.bolt.ui.theme.BoltTheme
import com.ghost.bolt.utils.TmdbConfig
import com.ghost.bolt.utils.mapper.toUiMediaDetail
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

fun formatVoteCount(voteCount: Int): String {
    if (voteCount <= 0) return ""

    val suffix = when {
        voteCount < 1000 -> ""
        voteCount < 1000000 -> "K"
        voteCount < 1000000000 -> "M"
        else -> "B"
    }

    val value = when (suffix) {
        "K" -> (voteCount / 1000.0).roundToInt().toString()
        "M" -> (voteCount / 1000000.0).roundToInt().toString()
        "B" -> (voteCount / 1000000000.0).roundToInt().toString()
        else -> voteCount.toString()
    }

    return "$value$suffix"
}

private fun Long?.toYear(): String? = this?.let {
    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).year.toString()
}


@Composable
fun DetailSuccessContent(
    detail: UiMediaDetail,
    onCastClick: (Int) -> Unit,
    onMediaClick: (media: MediaCardUiModel) -> Unit,
    onGenreClick: (GenreEntity) -> Unit,
    onKeywordClick: (KeywordEntity) -> Unit
) {
    val media = detail.media
    val year = remember(media.releaseDate) { media.releaseDate.toYear() }
    val finishYear = remember(media.finishDate) { media.finishDate.toYear() }


    Box {
        AsyncImage(
            model = TmdbConfig.getPosterUrl(media.posterPath, TmdbConfig.TMDbPosterSize.W500)
                ?: TmdbConfig.getBackdropUrl(media.posterPath),
            contentDescription = "background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // top spacing and some extra


            DetailHeaderSection(media)
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {

                Text(
                    text = media.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                MediaMetaRow(
                    year = year,
                    runtime = media.runtime,
                    adult = media.adult ?: false,
                    mediaType = media.mediaType.name,
                    finishYear = finishYear,
                    extra = null
                )
                Spacer(modifier = Modifier.height(16.dp))



                GenreSection(detail.genres, onGenreClick)

                OverviewSection(media.overview)

                KeywordSection(detail.keywords, onKeywordClick = onKeywordClick)

                CastSection(detail.cast, onCastClick)

                ExternalLinksSection(Modifier, media)

                RelatedMediaSection(
                    "Recommendations", detail.recommendations, onMediaClick
                )
                RelatedMediaSection(
                    "Similar", detail.similar, onMediaClick
                )


                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

}


@Composable
private fun DetailHeaderSection(media: MediaEntity) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        Color.Transparent,
                        MaterialTheme.colorScheme.surfaceContainer.copy(0.5f),
                        MaterialTheme.colorScheme.surfaceContainer.copy(0.8f)
                    ),
//                    startY = 0f
                )
            )
            .height(500.dp)
    ) {

        MediaRatingRow(
            voteAverage = media.voteAverage,
            voteCount = media.voteCount,
            source = media.mediaSource,
            modifier = Modifier
//                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomStart)
        )
    }
}


@Composable
private fun GradientOverlay(startY: Float = 300f) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surfaceContainer
                    ), startY = startY
                )
            )
    )
}

@Composable
private fun MediaRatingRow(
    voteAverage: Float?, voteCount: Int?, source: MediaSource, modifier: Modifier = Modifier
) {
    if (voteAverage == null || voteAverage <= 0f) return

    val formattedVoteCount = remember(voteCount) {
        voteCount?.let { formatVoteCount(it) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {

        Surface(
            shape = RoundedCornerShape(50),
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFC107), Color(0xFFFFA000)
                            )
                        ), shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Rating",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = String.format("%.1f", voteAverage),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )

                    Spacer(Modifier.width(6.dp))

                    Text(
                        text = source.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        formattedVoteCount?.let {
            Spacer(Modifier.width(8.dp))

            Text(
                text = "$it votes",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun MediaMetaRow(
    year: String?,
    finishYear: String?,
    mediaType: String?,
    extra: String?, // seasons, episodes, etc.
    runtime: Int?,
    adult: Boolean
) {
    val metaColor = MaterialTheme.colorScheme.onSurfaceVariant

    // Build metadata list dynamically
    val metaItems = buildList {

        mediaType?.let { add(it.uppercase()) }

        if (year != null) {
            if (finishYear != null && finishYear != year) {
                add("$year–$finishYear") // TV range
            } else {
                add(year) // Movie year
            }
        }

        runtime?.takeIf { it > 0 }?.let {
            val hours = it / 60
            val minutes = it % 60
            add(
                if (hours > 0) "${hours}h ${minutes}m"
                else "${minutes}m"
            )
        }

        extra?.let { add(it) } // "3 Seasons", "24 Episodes"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        // NSFW badge
        if (adult) {
            Surface(
                shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "18+",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(Modifier.width(8.dp))
        }

        // Render metadata with automatic separators
        metaItems.forEachIndexed { index, item ->
            MetaText(item, metaColor)

            if (index < metaItems.lastIndex) {
                Spacer(Modifier.width(8.dp))
                MetaText("•", metaColor)
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}


@Composable
private fun MetaText(
    text: String, color: Color
) {
    Text(
        text = text, color = color, style = MaterialTheme.typography.bodyLarge
    )
}


@Composable
fun ExternalLinksSection(modifier: Modifier = Modifier, media: MediaEntity) {
    Text(
        text = "External Links",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!media.imdbId.isNullOrBlank()) {
            ExternalLinkButton(
                painter = painterResource(R.drawable.movie),
                label = "IMDb",
                url = "https://www.imdb.com/title/${media.imdbId}"
            )
        }

        ExternalLinkButton(
            painter = painterResource(R.drawable.link),
            label = "TMDB",
            url = TmdbConfig.getTmdbUrl(media.mediaType, media.id)
        )

        if (!media.homepage.isNullOrBlank()) {
            ExternalLinkButton(
                painter = painterResource(R.drawable.language),
                label = "Website",
                url = media.homepage
            )
        }
    }
}

@Composable
private fun GenreSection(
    genres: List<GenreEntity>, onGenreClick: (GenreEntity) -> Unit
) {
    if (genres.isEmpty()) return

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(genres) { genre ->
            SuggestionChip(
                onClick = { onGenreClick(genre) },
                label = { Text(genre.name) },
                shape = RoundedCornerShape(percent = 50),
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun KeywordSection(
    keywords: List<KeywordEntity>, onKeywordClick: (KeywordEntity) -> Unit, previewCount: Int = 6
) {
    if (keywords.isEmpty()) return

    var expanded by remember { mutableStateOf(false) }

    val previewKeywords = keywords.take(previewCount)
    val remainingKeywords = keywords.drop(previewCount)

    Column {

        Text(
            text = "Keywords",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 Preview Row
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            previewKeywords.forEach { keyword ->
                KeywordChip(
                    text = "#${keyword.keywords}", onClick = { onKeywordClick(keyword) })
            }

            if (!expanded && remainingKeywords.isNotEmpty()) {
                KeywordChip(
                    text = "+${remainingKeywords.size}",
                    isExpandChip = true,
                    onClick = { expanded = true })
            }
        }

        // 🔹 Expanded Section
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    remainingKeywords.forEach { keyword ->
                        KeywordChip(
                            text = "#${keyword.keywords}", onClick = { onKeywordClick(keyword) })
                    }
                    KeywordChip(
                        text = "Show less", isExpandChip = true, onClick = { expanded = false })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Composable
private fun KeywordChip(
    text: String, onClick: () -> Unit, isExpandChip: Boolean = false
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isExpandChip) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable { onClick() }) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isExpandChip) MaterialTheme.colorScheme.onPrimaryContainer
            else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                horizontal = 14.dp, vertical = 8.dp
            )
        )
    }
}


@Composable
private fun OverviewSection(overview: String?) {

    if (overview.isNullOrBlank()) return
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.animateContentSize()) {

        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))
        SelectionContainer {
            Text(
                text = overview,
                maxLines = if (isExpanded) Int.MAX_VALUE else 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { isExpanded = !isExpanded })
        }

    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun RelatedMediaSection(
    title: String, relatedMediaItems: List<UiRelatedMedia>, onMediaClick: (MediaCardUiModel) -> Unit
) {
    if (relatedMediaItems.isEmpty()) return

    Text(title, style = MaterialTheme.typography.titleMedium)

    Spacer(Modifier.height(12.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(relatedMediaItems) { rec ->
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    MediaEntityCard(
                        entity = rec.media,
                        mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                        onMediaClick = onMediaClick,
                    )
                }

            }

        }
    }
}


@Composable
fun ExternalLinkButton(
    painter: Painter, label: String, url: String
) {
    val context = LocalContext.current
    AssistChip(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }, label = { Text(label) }, leadingIcon = {
        Icon(painter, contentDescription = null, modifier = Modifier.size(16.dp))
    })
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SuccessContentPreview() {
    val sampleMedia = MediaEntity(
        id = 1,
        title = "Sample Movie Title",
        overview = "This is a sample overview for a movie. It's quite an interesting movie, you should watch it. It has a lot of action, adventure, and drama. The actors are great and the story is compelling.",
        posterPath = "/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg",
        backdropPath = "/5YZbUmjbMa3Clvry1UAeVGQFbZs.jpg",
        releaseDate = 1672531200000L, // Jan 1, 2023
        voteAverage = 8.5f,
        mediaType = AppMediaType.MOVIE,
        runtime = 148,
        imdbId = "tt1234567",
        homepage = "https://www.google.com",
        voteCount = 1234,
        status = null,
        finishDate = null,
        revenue = null,
        adult = !false,
        budget = null,
        originalLanguage = "en",
        originalTitle = "Sample Movie Title",
        popularity = 123.45f,
        tagline = "A sample tagline.",
        episodes = null,
        themeColor = null,
        mediaSource = MediaSource.TMDB
    )

    val sampleGenres = listOf(
        GenreEntity(1, "Action"), GenreEntity(2, "Adventure"), GenreEntity(3, "Science Fiction")
    )

    val sampleKeywords = listOf(
        KeywordEntity(1, "Action"),
        KeywordEntity(2, "Adventure"),
        KeywordEntity(3, "Science Fiction"),
        KeywordEntity(4, "Thriller"),
        KeywordEntity(5, "Drama"),
        KeywordEntity(6, "Mystery"),
        KeywordEntity(1, "Action"),
        KeywordEntity(2, "Adventure"),
        KeywordEntity(3, "Science Fiction"),
        KeywordEntity(4, "Thriller"),
        KeywordEntity(5, "Drama"),
        KeywordEntity(6, "Mystery")
    )

    val sampleCast = listOf(
        CastEntity(
            castId = 1,
            name = "Actor One",
            profilePath = "/path1.jpg",
            knownForDepartment = "Acting"
        ), CastEntity(
            castId = 2,
            name = "Actor Two",
            profilePath = "/path2.jpg",
            knownForDepartment = "Acting"
        ), CastEntity(
            castId = 3,
            name = "Actor Three",
            profilePath = "/path3.jpg",
            knownForDepartment = "Acting"
        )
    )

    val sampleRecommendations = listOf(
        MediaEntity(
            id = 2,
            title = "Another Movie",
            posterPath = "/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg",
            mediaType = AppMediaType.MOVIE,
            voteAverage = null,
            overview = null,
            releaseDate = null,
            voteCount = null,
            status = null,
            finishDate = null,
            revenue = null,
            runtime = null,
            adult = null,
            backdropPath = null,
            budget = null,
            homepage = null,
            imdbId = null,
            originalLanguage = null,
            originalTitle = null,
            popularity = null,
            tagline = null,
            episodes = null,
            themeColor = null,
            mediaSource = MediaSource.TMDB
        ), MediaEntity(
            id = 3,
            title = "A Third Film",
            posterPath = "/qNBAXBIQlnOThrVvA6mA2B5ggV6.jpg",
            mediaType = AppMediaType.MOVIE,
            voteAverage = null,
            overview = null,
            releaseDate = null,
            voteCount = null,
            status = null,
            finishDate = null,
            revenue = null,
            runtime = null,
            adult = null,
            backdropPath = null,
            budget = null,
            homepage = null,
            imdbId = null,
            originalLanguage = null,
            originalTitle = null,
            popularity = null,
            tagline = null,
            episodes = null,
            themeColor = null,
            mediaSource = MediaSource.TMDB
        )
    )

    val sampleDetail = MediaDetail(
        media = sampleMedia,
        genres = sampleGenres,
        cast = sampleCast,
        recommendations = sampleRecommendations,
        similar = sampleRecommendations,
        keywords = sampleKeywords,
        productionCompanies = emptyList(),
        spokenLanguages = emptyList(),
    )

    BoltTheme {
        DetailSuccessContent(
            detail = sampleDetail.toUiMediaDetail(emptyList(), emptyList(), emptyList()),
            onCastClick = {},
            onMediaClick = { },
            onGenreClick = { },
            onKeywordClick = { })
    }
}


