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

android {
    namespace = "com.ram.local_weather"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ram.local_weather"
        minSdk = 28
        targetSdk = 36
        versionCode = 3
        versionName = "1.2"

        testInstrumentationRunner = "com.ram.local_weather.CustomHiltTestRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //added for hilt dependency
    implementation("com.google.dagger:hilt-android:2.52")
    implementation(libs.androidx.hilt.common)
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    testImplementation(libs.junit.junit)
//    implementation(libs.androidx.hilt.work) // Check latest version
    ksp("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-work:1.2.0") // compatible with WorkManager 2.9.0
    ksp("androidx.hilt:hilt-compiler:1.2.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Check latest version


    //Coil - For loading internet images in jetpack compose similar to glide
    implementation("io.coil-kt:coil-compose:2.6.0")
    //added for composable screen navigation
    implementation("androidx.navigation:navigation-compose:2.9.2")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //work manager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    testImplementation(libs.junit)
    // Mockito core (required)
    testImplementation("org.mockito:mockito-core:5.12.0")
    // Mockito-Kotlin extension (adds better Kotlin syntax & null-safety)
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    // Coroutines test utilities (for testing suspend functions)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    //added for instantexecutor rule

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    //GSON converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    //MOCK Webserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.52")
    kspTest("com.google.dagger:hilt-compiler:2.52")
    testImplementation("org.mockito:mockito-inline:5.2.0")



    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Coroutines test utilities (for testing suspend functions)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Hilt testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kspAndroidTest("com.google.dagger:hilt-compiler:2.52")
    // Mockito core (required)
//    androidTestImplementation("org.mockito:mockito-core:5.12.0")
    androidTestImplementation("org.mockito:mockito-android:5.12.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
// Mockito-Kotlin extension (adds better Kotlin syntax & null-safety)
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")

    debugImplementation("androidx.compose.runtime:runtime-tracing:1.0.0-beta01")
}