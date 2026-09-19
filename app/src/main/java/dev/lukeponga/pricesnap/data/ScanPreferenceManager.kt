package dev.lukeponga.pricesnap.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

private val Context.dataStore by preferencesDataStore(name = "scan_prefs")

class ScanPreferenceManager(private val context: Context) {
    private object Keys {
        val DAILY_SCAN_COUNT = intPreferencesKey("daily_scan_count")
        val LAST_SCAN_DATE = stringPreferencesKey("last_scan_date")
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val dailyScanCount: Flow<Int> = context.dataStore.data.map { preferences ->
        val lastDate = preferences[Keys.LAST_SCAN_DATE] ?: ""
        val currentDate = dateFormat.format(Date())
        
        if (lastDate != currentDate) {
            0
        } else {
            preferences[Keys.DAILY_SCAN_COUNT] ?: 0
        }
    }

    suspend fun incrementScanCount() {
        context.dataStore.edit { preferences ->
            val lastDate = preferences[Keys.LAST_SCAN_DATE] ?: ""
            val currentDate = dateFormat.format(Date())
            
            val currentCount = if (lastDate != currentDate) 0 else (preferences[Keys.DAILY_SCAN_COUNT] ?: 0)
            
            preferences[Keys.DAILY_SCAN_COUNT] = currentCount + 1
            preferences[Keys.LAST_SCAN_DATE] = currentDate
        }
    }
}
