package com.example.awwal.domain.classes

/**
 * Represents a user's prayer time settings
 */
data class PrayerTimeSettings(
    val latitude: Double = 51.5074, // Default: London
    val longitude: Double = -0.1278,
    val calculationMethod: CalculationMethodType = CalculationMethodType.MUSLIM_WORLD_LEAGUE,
    val madhab: MadhabType = MadhabType.SHAFI,
    val highLatitudeRule: HighLatitudeRuleType = HighLatitudeRuleType.MIDDLE_OF_THE_NIGHT
)

/**
 * Calculation methods supported by Adhan
 */
enum class CalculationMethodType(val displayName: String) {
    MUSLIM_WORLD_LEAGUE("Muslim World League"),
    EGYPTIAN("Egyptian General Authority"),
    KARACHI("University of Islamic Sciences, Karachi"),
    UMM_AL_QURA("Umm al-Qura University, Makkah"),
    DUBAI("Dubai"),
    MOON_SIGHTING_COMMITTEE("Moon Sighting Committee"),
    NORTH_AMERICA("Islamic Society of North America"),
    KUWAIT("Kuwait"),
    QATAR("Qatar"),
    SINGAPORE("Singapore")
}

/**
 * Madhab types for Asr calculation
 */
enum class MadhabType(val displayName: String) {
    SHAFI("Shafi (Standard)"),
    HANAFI("Hanafi")
}

/**
 * High latitude rules for extreme latitudes
 */
enum class HighLatitudeRuleType(val displayName: String) {
    MIDDLE_OF_THE_NIGHT("Middle of the Night"),
    SEVENTH_OF_THE_NIGHT("Seventh of the Night"),
    TWILIGHT_ANGLE("Twilight Angle")
}

