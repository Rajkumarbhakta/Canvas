package com.rkbapps.canvas.di.modules

import com.rkbapps.canvas.ui.screens.drawing.DrawingRepository
import com.rkbapps.canvas.ui.screens.drawing.DrawingViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val provideRepositories = module {
    singleOf(::DrawingRepository)
}

val provideViewModels = module{
    viewModelOf(::DrawingViewModel)
}