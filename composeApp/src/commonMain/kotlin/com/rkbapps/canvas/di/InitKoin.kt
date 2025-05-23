package com.rkbapps.canvas.di

import com.rkbapps.canvas.di.modules.*
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration?=null){
    startKoin {
        config?.invoke(this)
        modules(provideRepositories, provideViewModels,platformModule,dbModule)
    }
}