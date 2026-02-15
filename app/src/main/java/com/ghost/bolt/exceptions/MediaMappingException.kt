package com.ghost.bolt.exceptions

import com.ghost.bolt.enums.AppCategory
import com.ghost.bolt.enums.AppMediaType
import com.ghost.bolt.enums.MediaProvider

sealed class MediaMappingException(message: String) : IllegalArgumentException(message)

class InvalidMediaTypeException(
    val provider: MediaProvider,
    val mediaType: AppMediaType
) : MediaMappingException(
    "Invalid or unsupported media type '$mediaType' for provider '$provider'"
)


class InvalidMediaCategoryException(
    val provider: MediaProvider,
    val category: AppCategory
) : MediaMappingException(
    "Invalid or unsupported media category '$category' for provider '$provider'"
)



