package com.samadtch.bilinguai.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) = startKoin {
    appDeclaration()
    modules(
        getNativeAppModule(),
        appModule,
        dataModule,
        viewmodelModule
    )
}

//To call by IOS
fun initKoin() = initKoin {}