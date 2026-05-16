package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.db.DbOperations
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
    single { DbOperations(get()) }
    single { PreferenceManager(get()) }
}

val provideRepositories = module {
    factoryOf(::DrawingRepository)
    factory { HomeRepository(get()) }
    factory { SettingsRepository(get()) }
}

val provideViewModels = module{
    viewModelOf(::DrawingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::SettingsViewModel)
}