package com.rkbapps.canvas.di.modules

import android.content.Context
import com.rkbapps.canvas.db.DbManager
import com.rkbapps.canvas.db.DbManagerImpl
import com.rkbapps.canvas.db.SqliteDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule = module {
    single<DbManager> { DbManagerImpl(context = get()) }
    single { 
        val database = SqliteDatabase()
        database.initialize(get<Context>())
        database
    }
}