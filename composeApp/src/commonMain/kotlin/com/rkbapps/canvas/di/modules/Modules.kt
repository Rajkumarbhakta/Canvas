package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.db.DbOperations
import com.rkbapps.canvas.ui.screens.drawing.DrawingRepository
import com.rkbapps.canvas.ui.screens.drawing.DrawingViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


expect val platformModule: Module

val dbModule = module {
    single { DbOperations(get()) }
}

val provideRepositories = module {
    singleOf(::DrawingRepository)
}

val provideViewModels = module{
    viewModelOf(::DrawingViewModel)
}