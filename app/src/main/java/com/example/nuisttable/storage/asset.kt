package com.example.nuisttable.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nuisttable.storage.data.TimetableCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException

private const val DATASTORE_NAME = "timetable_cache"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
private val TIMETABLE_CACHE_KEY = stringPreferencesKey("timetable_cache_json")

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

suspend fun hasTimetableCache(context: Context): Boolean {
    return context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            !preferences[TIMETABLE_CACHE_KEY].isNullOrBlank()
        }
        .first()
}

suspend fun saveTimetableCache(context: Context, cache: TimetableCache) {
    context.dataStore.edit { preferences ->
        preferences[TIMETABLE_CACHE_KEY] = json.encodeToString(cache)
    }
}

suspend fun clearTimetableCache(context: Context) {
    context.dataStore.edit { preferences ->
        preferences.remove(TIMETABLE_CACHE_KEY)
    }
}
