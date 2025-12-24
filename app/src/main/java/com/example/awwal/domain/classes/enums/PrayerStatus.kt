package com.example.awwal.domain.classes.enums

enum class PrayerStatus(override val value: Int) : EnumWithValue<Int> {
    PRAYED(0),
    MISSED(1),
    LATE(2),
    EMPTY(3);
}