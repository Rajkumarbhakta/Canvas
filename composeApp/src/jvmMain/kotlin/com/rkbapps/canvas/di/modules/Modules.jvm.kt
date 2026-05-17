package com.rkbapps.canvas.di.modules

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
import com.rkbapps.canvas.util.ImageSharerJvm
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    singleOf<DbManager>(::DbManagerImpl)
    singleOf<ImageSharer>(::ImageSharerJvm)
    single <DataStore<Preferences>?>{
        PreferenceDataStoreFactory.create(
            storage = FileStorage(
                serializer = PreferencesFileSerializer,
                produceFile = {
                    File(System.getProperty("java.io.tmpdir"), DATASTORE_FILE_NAME)
                }
            ),
        )
    }
    single <AppDatabase>{ getDatabaseBuilder().build() }
}