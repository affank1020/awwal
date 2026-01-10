package com.example.awwal.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.CalculationParameters
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes as AdhanPrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.example.awwal.domain.PrayerTimesRepository
import com.example.awwal.domain.classes.CalculationMethodType
import com.example.awwal.domain.classes.HighLatitudeRuleType
import com.example.awwal.domain.classes.MadhabType
import com.example.awwal.domain.classes.PrayerTimeSettings
import com.example.awwal.domain.classes.PrayerTimes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

private val Context.prayerTimesDataStore: DataStore<Preferences> by preferencesDataStore(name = "prayer_times_settings")

class PrayerTimesRepositoryImpl(
    private val context: Context
) : PrayerTimesRepository {

    private object PreferencesKeys {
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val CALCULATION_METHOD = stringPreferencesKey("calculation_method")
        val MADHAB = stringPreferencesKey("madhab")
        val HIGH_LATITUDE_RULE = stringPreferencesKey("high_latitude_rule")
    }

    override fun getPrayerTimesForDate(date: LocalDate): PrayerTimes {
        // Get current settings synchronously for calculation
        val settings = runBlocking { getSettings().first() }

        val coordinates = Coordinates(settings.latitude, settings.longitude)
        val params = getCalculationParameters(settings)

        val dateComponents = DateComponents(date.year, date.monthValue, date.dayOfMonth)
        val adhanTimes = AdhanPrayerTimes(coordinates, dateComponents, params)

        return PrayerTimes(
            date = date,
            fajr = dateToLocalTime(adhanTimes.fajr),
            sunrise = dateToLocalTime(adhanTimes.sunrise),
            dhuhr = dateToLocalTime(adhanTimes.dhuhr),
            asr = dateToLocalTime(adhanTimes.asr),
            maghrib = dateToLocalTime(adhanTimes.maghrib),
            isha = dateToLocalTime(adhanTimes.isha)
        )
    }

    override fun getSettings(): Flow<PrayerTimeSettings> {
        return context.prayerTimesDataStore.data.map { preferences ->
            PrayerTimeSettings(
                latitude = preferences[PreferencesKeys.LATITUDE] ?: 51.5074,
                longitude = preferences[PreferencesKeys.LONGITUDE] ?: -0.1278,
                calculationMethod = preferences[PreferencesKeys.CALCULATION_METHOD]?.let {
                    try { CalculationMethodType.valueOf(it) } catch (e: Exception) { CalculationMethodType.MUSLIM_WORLD_LEAGUE }
                } ?: CalculationMethodType.MUSLIM_WORLD_LEAGUE,
                madhab = preferences[PreferencesKeys.MADHAB]?.let {
                    try { MadhabType.valueOf(it) } catch (e: Exception) { MadhabType.SHAFI }
                } ?: MadhabType.SHAFI,
                highLatitudeRule = preferences[PreferencesKeys.HIGH_LATITUDE_RULE]?.let {
                    try { HighLatitudeRuleType.valueOf(it) } catch (e: Exception) { HighLatitudeRuleType.MIDDLE_OF_THE_NIGHT }
                } ?: HighLatitudeRuleType.MIDDLE_OF_THE_NIGHT
            )
        }
    }

    override suspend fun updateSettings(settings: PrayerTimeSettings) {
        context.prayerTimesDataStore.edit { preferences ->
            preferences[PreferencesKeys.LATITUDE] = settings.latitude
            preferences[PreferencesKeys.LONGITUDE] = settings.longitude
            preferences[PreferencesKeys.CALCULATION_METHOD] = settings.calculationMethod.name
            preferences[PreferencesKeys.MADHAB] = settings.madhab.name
            preferences[PreferencesKeys.HIGH_LATITUDE_RULE] = settings.highLatitudeRule.name
        }
    }

    override suspend fun updateLocation(latitude: Double, longitude: Double) {
        context.prayerTimesDataStore.edit { preferences ->
            preferences[PreferencesKeys.LATITUDE] = latitude
            preferences[PreferencesKeys.LONGITUDE] = longitude
        }
    }

    override suspend fun updateCalculationMethod(method: CalculationMethodType) {
        context.prayerTimesDataStore.edit { preferences ->
            preferences[PreferencesKeys.CALCULATION_METHOD] = method.name
        }
    }

    override suspend fun updateMadhab(madhab: MadhabType) {
        context.prayerTimesDataStore.edit { preferences ->
            preferences[PreferencesKeys.MADHAB] = madhab.name
        }
    }

    private fun getCalculationParameters(settings: PrayerTimeSettings): CalculationParameters {
        val method = when (settings.calculationMethod) {
            CalculationMethodType.MUSLIM_WORLD_LEAGUE -> CalculationMethod.MUSLIM_WORLD_LEAGUE
            CalculationMethodType.EGYPTIAN -> CalculationMethod.EGYPTIAN
            CalculationMethodType.KARACHI -> CalculationMethod.KARACHI
            CalculationMethodType.UMM_AL_QURA -> CalculationMethod.UMM_AL_QURA
            CalculationMethodType.DUBAI -> CalculationMethod.DUBAI
            CalculationMethodType.MOON_SIGHTING_COMMITTEE -> CalculationMethod.MOON_SIGHTING_COMMITTEE
            CalculationMethodType.NORTH_AMERICA -> CalculationMethod.NORTH_AMERICA
            CalculationMethodType.KUWAIT -> CalculationMethod.KUWAIT
            CalculationMethodType.QATAR -> CalculationMethod.QATAR
            CalculationMethodType.SINGAPORE -> CalculationMethod.SINGAPORE
        }

        val params = method.parameters
        params.madhab = when (settings.madhab) {
            MadhabType.SHAFI -> Madhab.SHAFI
            MadhabType.HANAFI -> Madhab.HANAFI
        }

        return params
    }

    private fun dateToLocalTime(date: Date): LocalTime {
        return date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    }
}

