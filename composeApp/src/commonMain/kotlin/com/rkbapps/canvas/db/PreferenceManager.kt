package com.rkbapps.canvas.db

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PreferenceManager(private val dataStore: DataStore<Preferences>?) {

    companion object {
        internal const val DATASTORE_FILE_NAME = "settings.preferences_pb"
        val IS_SYSTEM_THEME = booleanPreferencesKey("is_system_theme")
        val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val COLOR_THEME = intPreferencesKey("color_theme")
        val IS_MIGRATED_TO_ROOM = booleanPreferencesKey("is_migrated_to_room")
    }

//    suspend fun <T> saveObject(key: Preferences.Key<String>, value: T) {
//        val json = json.encodeToString(value)
//        context.dataStore.edit { preferences ->
//            preferences[key] = json
//        }
//    }
//
//    suspend fun <T> getObjectSynchronous(key: Preferences.Key<String>, classOfT: Class<T>): T? {
//        val json = try {
//            context.dataStore.data.first()[key]
//        }catch (e: Exception){
//            null
//        }
//        return gson.fromJson(json, classOfT)
//    }


//    fun <T>getObject(key: Preferences.Key<String>, classOfT: Class<T>): Flow<T> = dataStore.data
//        .catch {emit(emptyPreferences())}
//        .map {
//            val json = it[key]
//            gson.fromJson(json, classOfT)
//        }

    fun getLongPreference(key:Preferences.Key<Long>, defaultValue: Long? = 0) = dataStore?.data
        ?.catch {emit(emptyPreferences())}
        ?.map {
            it[key]?:defaultValue
        } ?: flowOf(defaultValue)


    suspend fun getLongPreferenceSynchronous(
        key: Preferences.Key<Long>,
        defaultValue: Long? = 0
    ): Long? {
        return try {
            dataStore?.data?.first()[key] ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }

    suspend fun saveLongPreference(key:Preferences.Key<Long>, value:Long){
        dataStore?.edit {preferences->
            preferences[key] = value
        }
    }


    fun getIntPreference(key:Preferences.Key<Int>, defaultValue: Int = 0) = dataStore?.data
        ?.catch {emit(emptyPreferences())}
        ?.map {
            it[key]?:defaultValue
        }  ?: flowOf(defaultValue)
    suspend fun saveIntPreference(key:Preferences.Key<Int>, value:Int){
        dataStore?.edit {preferences->
            preferences[key] = value
        }
    }
    fun getStringPreference(key:Preferences.Key<String>, defaultValue: String? = null) = dataStore?.data
        ?.catch {emit(emptyPreferences())}
        ?.map {
            it[key]?:defaultValue
        }  ?: flowOf(defaultValue)

    suspend fun saveStringPreference(key:Preferences.Key<String>, value:String){
        dataStore?.edit {preferences->
            preferences[key] = value
        }
    }

    suspend fun getStringPreferenceSynchronous(
        key: Preferences.Key<String>,
        defaultValue: String? = null
    ): String? {
        return try {
            dataStore?.data?.first()[key] ?: defaultValue
        } catch (e: Exception) {
            defaultValue
        }
    }



    fun getBooleanPreference(key: Preferences.Key<Boolean>, defaultValue: Boolean) = dataStore?.data
        ?.catch {emit(emptyPreferences())}
        ?.map {
            it[key]?:defaultValue
        } ?: flowOf(defaultValue)

    suspend fun saveBooleanPreference(key:Preferences.Key<Boolean>, value:Boolean){
        dataStore?.edit {preferences->
            preferences[key] = value
        }
    }


    /**
     * Clears all preferences stored in the DataStore.
     * This will remove all stored data, including user authentication status,
     * last selected project ID, and any other saved preferences. Use this with caution.
     * Should be called from a coroutine due to the suspend modifier.
     */
    suspend fun clearPreferences() {
        dataStore?.edit { preferences -> preferences.clear() }
    }


    /**
     * Clears the preference value associated with the given key from DataStore.
     * This is useful for deleting specific preferences without removing all data.
     * Should be called from a coroutine due to the suspend modifier.
     *
     * @param key The preference key whose data needs to be removed.
     */
    suspend fun <T>clearPreference(key:Preferences.Key<T>){
        dataStore?.edit {preferences-> preferences.remove(key) }
    }


}