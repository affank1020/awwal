package com.example.awwal.domain.classes.enums

enum class ThemeMode(override val value: Int) : EnumWithValue<Int> {
    LIGHT(0),
    DARK(1),
    AUTO(2);
}