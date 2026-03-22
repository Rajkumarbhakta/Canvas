package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.db.DbManager
import com.rkbapps.canvas.db.DbManagerImpl
import com.rkbapps.canvas.util.ImageSharer
import com.rkbapps.canvas.util.ImageSharerJvm
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule= module {
    singleOf<DbManager>(::DbManagerImpl)
    singleOf<ImageSharer>(::ImageSharerJvm)
}