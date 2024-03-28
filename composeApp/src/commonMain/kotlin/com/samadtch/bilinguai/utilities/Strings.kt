package com.samadtch.bilinguai.utilities

import dev.icerock.moko.resources.StringResource

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Strings {
    fun get(id: StringResource, args: List<Any>): String
}