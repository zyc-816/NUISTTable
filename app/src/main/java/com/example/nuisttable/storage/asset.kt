package com.example.nuisttable.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nuisttable.storage.data.TimetableCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DATASTORE_NAME = "timetable_cache"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
private val TIMETABLE_CACHE_KEY = stringPreferencesKey("timetable_cache_json")
private val IS_DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

fun getTimetableCache(context: Context): Flow<TimetableCache?> {
    return context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val cacheJson = preferences[TIMETABLE_CACHE_KEY] ?: return@map null
            runCatching {
                json.decodeFromString<TimetableCache>(cacheJson)
            }.getOrNull()
        }
}

fun getIsDarkMode(context: Context, defaultValue: Boolean): Flow<Boolean> {
    return context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[IS_DARK_MODE_KEY] ?: defaultValue
        }
}

suspend fun saveTimetableCache(context: Context, cache: TimetableCache) {
    context.dataStore.edit { preferences ->
        preferences[TIMETABLE_CACHE_KEY] = json.encodeToString(cache)
    }
}

suspend fun saveIsDarkMode(context: Context, isDarkMode: Boolean) {
    context.dataStore.edit { preferences ->
        preferences[IS_DARK_MODE_KEY] = isDarkMode
    }
}

suspend fun clearTimetableCache(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(TIMETABLE_CACHE_KEY)
    }
}
