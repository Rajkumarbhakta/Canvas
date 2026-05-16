package com.rkbapps.canvas.di.modules

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rkbapps.canvas.db.DbManager
import com.rkbapps.canvas.db.DbManagerImpl
import com.rkbapps.canvas.util.ImageSharer
import com.rkbapps.canvas.util.ImageSharerWeb
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf<DbManager>(::DbManagerImpl)
    singleOf<ImageSharer>(::ImageSharerWeb)
    single<DataStore<Preferences>?> { null }
}

