package com.ghost.bolt.enums

import com.ghost.bolt.enums.tdmb.TmdbMediaType

enum class AppMediaType {
    MOVIE,
    TV,
    ANIME;
}


fun AppMediaType.toTmdbMediaType(): TmdbMediaType? {
    return when (this) {
        AppMediaType.MOVIE -> TmdbMediaType.MOVIE
        AppMediaType.TV -> TmdbMediaType.TV
        AppMediaType.ANIME -> null
    }
}

