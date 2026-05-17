package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.db.AppDatabase
import com.rkbapps.canvas.db.migrator.DataMigrationManager
import com.rkbapps.canvas.db.old_db.DbManager
import com.rkbapps.canvas.db.PreferenceManager
import com.rkbapps.canvas.ui.screens.drawing.DrawingRepository
import com.rkbapps.canvas.ui.screens.drawing.DrawingViewModel
import com.rkbapps.canvas.ui.screens.home.HomeRepository
import com.rkbapps.canvas.ui.screens.home.HomeViewModel
import com.rkbapps.canvas.ui.screens.settings.SettingsRepository
import com.rkbapps.canvas.ui.screens.settings.SettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


expect val platformModule: Module

val dbModule = module {
    single { get<AppDatabase>().drawingDao() }
    single { PreferenceManager(get()) }
    single { DataMigrationManager(get(), get(), get()) }
}


val provideRepositories = module {
    factoryOf(::DrawingRepository)
    factory { HomeRepository(get()) }
    factory { SettingsRepository(get(),get()) }
}

val provideViewModels = module{
    viewModelOf(::DrawingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}