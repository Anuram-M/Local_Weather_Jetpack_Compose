import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.ram.local_weather"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ram.local_weather"
        minSdk = 28
        targetSdk = 37
        versionCode = 9
        versionName = "1.8"

        testInstrumentationRunner = "com.ram.local_weather.CustomHiltTestRunner"
    }

    signingConfigs {
        create("release") {

            val keystorePath = System.getenv("RELEASE_KEYSTORE_PATH")
                ?: localProperties.getProperty("RELEASE_KEYSTORE_PATH")

//            storeFile = rootProject.file(keystorePath)
            storeFile = if (keystorePath != null) {
                val pathFile = File(keystorePath)

                // 1. Resolve the file based on whether the path is absolute or relative
                val resolvedFile = if (pathFile.isAbsolute) {
                    pathFile
                } else {
                    // Your snippet for relative paths
                    val rootFile = rootProject.file(keystorePath)
                    println("Root file: exists - ${rootFile.exists()}")
                    if (rootFile.exists()) rootFile else file(keystorePath)
                }

                // 2. Print diagnostic info to the CI/CD console logs
                val pathType = if (pathFile.isAbsolute) "ABSOLUTE" else "RELATIVE"
                println("====== SIGNING CONFIG DIAGNOSTICS ======")
                println("Configured Path: $keystorePath ($pathType)")
                println("========================================")

                resolvedFile
            } else {
                println("====== SIGNING CONFIG WARNING ======")
                println("RELEASE_KEYSTORE_PATH is null!")
                println("====================================")
                null
            }
            storePassword = System.getenv("RELEASE_KEYSTORE_PASSWORD")
                ?: localProperties.getProperty("RELEASE_KEYSTORE_PASSWORD")

            keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                ?: localProperties.getProperty("RELEASE_KEY_ALIAS")

            keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
                ?: localProperties.getProperty("RELEASE_KEY_PASSWORD")

        }
    }

    buildTypes {
        debug {
            if (System.getenv("GITHUB_ACTIONS") != "true") {
                applicationIdSuffix = ".dev"
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                mappingFileUploadEnabled = false
            }
            extra.set("enableCrashlytics", false)
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // ADD THIS LINE TO BYPASS THE NETWORK ERROR
            configure<com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension> {
                nativeSymbolUploadEnabled = false
            }

            signingConfig = signingConfigs.getByName("release")
        }

        create("benchmark") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
            applicationIdSuffix = ".benchmark"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    lint {
        // Prevents Lint errors from killing the build process
        abortOnError = false
        checkReleaseBuilds = false
    }
}

dependencies {
    implementation(project(":core-domain"))
    implementation(project(":core-network"))
    implementation(project(":core-database"))
    implementation(project(":core-firebase"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    //added to use the location services
    implementation(libs.gms.play.services.location)

    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit.junit)


    //added for hilt for di
    implementation(libs.com.google.dagger.hilt.android4)
    ksp(libs.com.google.dagger.hilt.compiler2)

    //work manager
    implementation(libs.work.runtime.ktx)

    //hilt for workmanager
    implementation(libs.androidx.hilt.work) // compatible with WorkManager 2.9.0
    ksp(libs.androidx.hilt.compiler)

    //navigation
    implementation(libs.hilt.navigation.compose) // Check latest version

    //Coil - For loading internet images in jetpack compose similar to glide
    implementation(libs.coil.kt.coil.compose)

    //retrofit
    implementation(libs.retrofit2.retrofit)

    //GSON converter
    implementation(libs.retrofit2.converter.gson)

    //added for composable screen navigation
    implementation(libs.navigation.compose)

    //agesignals - parental control
    implementation(libs.play.age.signals)



    //TESTING dependencies
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    testImplementation(libs.junit)
    // Mockito core (required)
    testImplementation(libs.mockito.mockito.core)
    // Mockito-Kotlin extension (adds better Kotlin syntax & null-safety)
    testImplementation(libs.kotlin.mockito.kotlin)
    // Coroutines test utilities (for testing suspend functions)
    testImplementation(libs.jetbrains.kotlinx.coroutines.test)
    //added for instantexecutor rule


    //MOCK Webserver
    testImplementation(libs.okhttp3.mockwebserver)
    testImplementation(libs.core.testing)
    testImplementation(libs.dagger.hilt.android.testing)
    kspTest(libs.com.google.dagger.hilt.compiler2)
    testImplementation(libs.mockito.mockito.inline)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.jetbrains.kotlinx.coroutines.test)  // Coroutines test utilities (for testing suspend functions)
    androidTestImplementation(libs.dagger.hilt.android.testing) // Hilt testing
    kspAndroidTest(libs.com.google.dagger.hilt.compiler2)
//    androidTestImplementation("org.mockito:mockito-core:5.12.0") // Mockito core (required)
    androidTestImplementation(libs.mockito.mockito.android)
    androidTestImplementation(libs.kotlin.mockito.kotlin)
    androidTestImplementation(libs.kotlin.mockito.kotlin)// Mockito-Kotlin extension (adds better Kotlin syntax & null-safety)
    androidTestImplementation(libs.core.testing)
    debugImplementation(libs.androidx.runtime.tracing)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // DataStore (Essential for managing Glance Widget State)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.room.paging) // Use your current Room version
    // Compose Paging Integration
    implementation(libs.androidx.paging.compose)
}