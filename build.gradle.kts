plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.mokoResources) apply false
}
buildscript {
    dependencies {
        classpath(libs.buildKonfig)
    }
}