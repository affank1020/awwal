package com.example.awwal.domain.classes.enums

enum class PrayerStatus(override val value: Int) : EnumWithValue<Int> {
    PRAYED(0),
    PRAYED_IN_MASJID(1),
    LATE(2),
    MISSED(3),
    EMPTY(4);
}

