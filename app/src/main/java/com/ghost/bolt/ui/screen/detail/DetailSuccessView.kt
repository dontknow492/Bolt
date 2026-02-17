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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ghost.bolt.R
import com.ghost.bolt.database.entity.CastEntity
import com.ghost.bolt.database.entity.GenreEntity
import com.ghost.bolt.database.entity.MediaDetail
import com.ghost.bolt.database.entity.MediaEntity
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaSource
import com.ghost.bolt.models.UiCastMember
import com.ghost.bolt.models.UiMediaDetail
import com.ghost.bolt.models.UiRelatedMedia
import com.ghost.bolt.ui.components.card.CoverVariant
import com.ghost.bolt.ui.components.card.MediaCardStyle
import com.ghost.bolt.ui.screen.Entity
import com.ghost.bolt.ui.theme.BoltTheme
import com.ghost.bolt.utils.TmdbConfig
import com.ghost.bolt.utils.mapper.toUiMediaDetail
import java.time.Instant
import java.time.ZoneId

@Composable
fun DetailSuccessContent(
    detail: UiMediaDetail,
    onCastClick: (Int) -> Unit,
    onMediaClick: (mediaId: Int, coverPath: String?, title: String?, backdropPath: String?) -> Unit,
    onGenreClick: (Int, String) -> Unit
) {
    val media = detail.media

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        DetailHeaderSection(media)

        Column(modifier = Modifier.padding(16.dp)) {

            GenreSection(detail.genres, onGenreClick)

            OverviewSection(media.overview)

            CastSection(detail.cast, onCastClick)

            ExternalLinksSection(Modifier, media)

            RecommendationSection(
                detail.recommendations,
                onMediaClick
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


@Composable
private fun DetailHeaderSection(media: MediaEntity) {

    val year = remember(media.releaseDate) {
        media.releaseDate?.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .year
                .toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {

        AsyncImage(
            model = TmdbConfig.getBackdropUrl(media.backdropPath ?: media.posterPath),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        GradientOverlay()

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {

            Text(
                text = media.title,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            MediaMetaRow(
                voteAverage = media.voteAverage,
                year = year,
                runtime = media.runtime
            )
        }
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
                        MaterialTheme.colorScheme.surfaceContainer
                    ),
                    startY = startY
                )
            )
    )
}

@Composable
private fun MediaMetaRow(
    voteAverage: Float?,
    year: String?,
    runtime: Int?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        voteAverage?.takeIf { it > 0 }?.let {
            Icon(
                imageVector = Icons.Rounded.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(String.format("%.1f", it), style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(12.dp))
        }

        year?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.width(12.dp))
        }

        runtime?.takeIf { it > 0 }?.let {
            Text(
                "${it / 60}h ${it % 60}m",
                color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
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
            url = "https://www.themoviedb.org/${media.mediaType?.name?.lowercase() ?: "movie"}/${media.id}"
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
    genres: List<GenreEntity>,
    onGenreClick: (Int, String) -> Unit
) {
    if (genres.isEmpty()) return

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(genres) { genre ->
            SuggestionChip(
                onClick = { onGenreClick(genre.genreId, genre.name) },
                label = { Text(genre.name) }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
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
                modifier = Modifier.clickable { isExpanded = !isExpanded }
            )
        }

    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun CastSection(
    cast: List<UiCastMember>,
    onCastClick: (Int) -> Unit
) {
    if (cast.isEmpty()) return

    Text("Top Cast", style = MaterialTheme.typography.titleMedium)

    Spacer(Modifier.height(12.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(cast.take(15)) {
            CastItem(it, onCastClick)
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun RecommendationSection(
    recommendations: List<UiRelatedMedia>,
    onMediaClick: (Int, String?, String?, String?) -> Unit
) {
    if (recommendations.isEmpty()) return

    Text("Recommendations", style = MaterialTheme.typography.titleMedium)

    Spacer(Modifier.height(12.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(recommendations) { rec ->
            SharedTransitionLayout {
                AnimatedVisibility(true) {
                    Entity(
                        entity = rec.media,
                        mediaStyle = MediaCardStyle.Cover(CoverVariant.NORMAL),
                        onMediaClick = onMediaClick,
                        animatedContentScope = this@AnimatedVisibility
                    )
                }

            }

        }
    }
}

@Composable
fun CastItem(person: UiCastMember, onClick: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick(person.id) }
    ) {
        AsyncImage(
            model = "https://image.tmdb.org/t/p/w185${person.profilePath}",
            contentDescription = person.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = person.name,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}


@Composable
fun ExternalLinkButton(
    painter: Painter,
    label: String,
    url: String
) {
    val context = LocalContext.current
    AssistChip(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        label = { Text(label) },
        leadingIcon = {
            Icon(painter, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    )
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
        adult = false,
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
        GenreEntity(1, "Action"),
        GenreEntity(2, "Adventure"),
        GenreEntity(3, "Science Fiction")
    )

    val sampleCast = listOf(
        CastEntity(
            castId = 1,
            name = "Actor One",
            profilePath = "/path1.jpg",
            knownForDepartment = "Acting"
        ),
        CastEntity(
            castId = 2,
            name = "Actor Two",
            profilePath = "/path2.jpg",
            knownForDepartment = "Acting"
        ),
        CastEntity(
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
        ),
        MediaEntity(
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
        keywords = emptyList(),
        productionCompanies = emptyList(),
        spokenLanguages = emptyList(),
        similar = emptyList()
    )

    BoltTheme {
        DetailSuccessContent(
            detail = sampleDetail.toUiMediaDetail(emptyList(), emptyList(), emptyList()),
            onCastClick = {},
            onMediaClick = { _, _, _, _ -> },
            onGenreClick = { _, _ -> }
        )
    }
}

