package com.ghost.bolt.enums.tdmb

enum class TDMBTimeWindow(private val value: String) {
    DAY("day"),
    WEEK("week");

    override fun toString() = value
}