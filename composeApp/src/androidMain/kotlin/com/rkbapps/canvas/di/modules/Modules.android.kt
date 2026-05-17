package com.rkbapps.canvas.di.modules

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.FileStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesFileSerializer
import com.rkbapps.canvas.db.AppDatabase
import com.rkbapps.canvas.db.old_db.DbManager
import com.rkbapps.canvas.db.old_db.DbManagerImpl
import com.rkbapps.canvas.db.PreferenceManager.Companion.DATASTORE_FILE_NAME
import com.rkbapps.canvas.db.getDatabaseBuilder
import com.rkbapps.canvas.util.ImageSharer
import com.rkbapps.canvas.util.ImageSharerAndroid
import org.koin.dsl.module

actual val platformModule = module {
    single<DbManager> { DbManagerImpl(context = get()) }
    single<ImageSharer> { ImageSharerAndroid(context = get()) }
    single<DataStore<Preferences>?>{
        PreferenceDataStoreFactory.create(
            storage =  FileStorage(
                serializer = PreferencesFileSerializer,
                produceFile = { get<Context>().filesDir.resolve(DATASTORE_FILE_NAME) }
            ),
        )
    }
    single <AppDatabase>{ getDatabaseBuilder(get()).build() }
}