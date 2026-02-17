package com.ghost.bolt.enums

import java.time.Instant
import java.time.temporal.ChronoUnit

enum class RefreshFrequency(
    val amount: Long,
    val unit: ChronoUnit
) {
    DAILY(1, ChronoUnit.DAYS),
    WEEKLY(7, ChronoUnit.DAYS),
    MONTHLY(1, ChronoUnit.MONTHS),
    YEARLY(1, ChronoUnit.YEARS);

    fun nextRefreshFrom(time: Instant): Instant {
        return time.plus(amount, unit)
    }
}
