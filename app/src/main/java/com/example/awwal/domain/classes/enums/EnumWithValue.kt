package com.example.awwal.domain.classes.enums

interface EnumWithValue<T> {
    val value: T
}

inline fun <reified E> enumFromValue(value: Int): E where E : Enum<E>, E : EnumWithValue<Int> {
    return enumValues<E>().find { it.value == value } ?: enumValues<E>().first()
}