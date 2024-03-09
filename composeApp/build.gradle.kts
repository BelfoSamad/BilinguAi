import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.ExperimentalComposeLibrary

fun DependencyHandlerScope.kapt(dependencyProvider : Provider<MinimalExternalModuleDependency>){
    add("kapt", dependencyProvider.get())
}

plugins {
    kotlin("kapt")
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.gms)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.mokoResources)
    id("com.codingfeline.buildkonfig")
}

buildkonfig {
    packageName = "com.samadtch.bilinguai"

    // default config is required
    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "APIKey", project.properties["APIKey"].toString())
    }
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        // Required for moko-resources to work
        applyDefaultHierarchyTemplate()

        androidMain {
            dependencies {
                //Compose
                implementation(libs.compose.ui.tooling.preview)
                implementation(libs.androidx.activity.compose)

                //Ads
                implementation(libs.ads)
                implementation(libs.ump)
                implementation(libs.ads.consent)

                //Firestore
                implementation(project.dependencies.platform(libs.firebase.android.bom))
                implementation(libs.firebase.android.auth)
                implementation(libs.firebase.android.firestore)
                implementation(libs.firebase.android.config)
                implementation(libs.firebase.android.crashlytics)

                //Koin
                implementation(libs.koin.android)

                //Others
                implementation(libs.review.ktx)
            }

            // Required for moko-resources to work
            dependsOn(commonMain.get())
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonMain.dependencies {
            //Compose
            implementation(libs.compose.ui.graphics)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.materialIconsExtended)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            //Ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            //Others
            api(libs.kmm.viewmodel.core)//KMM-ViewModel
            implementation(libs.coil.compose)//Coil
            implementation(libs.jsonpathkt)
            implementation(libs.koin.core)//Koin
            implementation(libs.datastore)//DataStore
            implementation(libs.kotlin.datetime)//DateTime
            implementation(libs.moko.resources.compose)//Moko Resources
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "com.samadtch.bilinguai"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.samadtch.bilinguai"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    viewBinding { enable = true }
    dependencies {
        //Compose
        implementation(libs.compose.lifecycle)
        debugImplementation(libs.compose.ui.tooling)

        //Components
        implementation(libs.androidx.fragment.ktx)
        implementation(libs.androidx.navigation.fragment.ktx)
        implementation(libs.androidx.navigation.ui.ktx)

        //Firebase
        implementation(project.dependencies.platform(libs.firebase.android.bom))
        implementation(libs.firebase.android.analytics)

        //Dependency Injection
        implementation(libs.koin.android)
        implementation(libs.hilt)
        implementation(libs.hilt.compose.navigation)
        kapt(libs.hilt.android.compiler)
        kapt(libs.hilt.compiler)

        //Splash Screen
        implementation(libs.androidx.splashscreen)
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.samadtch.bilinguai"
    multiplatformResourcesClassName = "Resources"
}

