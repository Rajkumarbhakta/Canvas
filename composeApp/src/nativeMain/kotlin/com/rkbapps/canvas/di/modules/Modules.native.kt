package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.db.DbManager
import com.rkbapps.canvas.db.DbManagerImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule= module {
    singleOf<DbManager>(::DbManagerImpl)
}