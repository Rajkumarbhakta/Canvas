package com.rkbapps.canvas.di.modules

import androidx.datastore.core.DataStore
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.rkbapps.canvas.db.AppDatabase
import com.rkbapps.canvas.db.old_db.DbManager
import com.rkbapps.canvas.db.old_db.DbManagerImpl
import com.rkbapps.canvas.db.PreferenceManager.Companion.DATASTORE_FILE_NAME
import com.rkbapps.canvas.db.getDatabaseBuilder
import com.rkbapps.canvas.util.ImageSharer
import com.rkbapps.canvas.util.ImageSharerNative
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule = module{
    singleOf<DbManager>(::DbManagerImpl)
    singleOf<ImageSharer>(::ImageSharerNative)
    single<DataStore<Preferences>?> {
        PreferenceDataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                serializer = PreferencesSerializer,
                producePath = {
                    val documentDirectory : NSURL? = NSFileManager.defaultManager.URLForDirectory(
                        directory = NSDocumentDirectory,
                        inDomain = NSUserDomainMask,
                        appropriateForURL = null,
                        create = false,
                        error = null,
                    )
                    (requireNotNull(documentDirectory).path + "/$DATASTORE_FILE_NAME").toPath()
                }
            ),
        )
    }
    single <AppDatabase>{ getDatabaseBuilder().build() }
}