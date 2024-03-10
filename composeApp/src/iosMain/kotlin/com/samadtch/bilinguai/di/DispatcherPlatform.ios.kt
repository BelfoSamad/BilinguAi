package com.samadtch.bilinguai.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

//TO Fix
class IOSDispatcher : Dispatcher {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO
}


internal actual fun provideDispatcher(): Dispatcher = IOSDispatcher()