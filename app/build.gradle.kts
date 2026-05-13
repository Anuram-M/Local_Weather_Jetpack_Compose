plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

android {
    namespace = "com.ram.local_weather"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ram.local_weather"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.ram.local_weather.CustomHiltTestRunner"
    }

    buildTypes {

        debug {
            isMinifyEnabled = true
            isShrinkResources = true
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
    }
}

dependencies {

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
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit.junit)
//    implementation(libs.androidx.hilt.work) // Check latest version
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-work:1.2.0") // compatible with WorkManager 2.9.0
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation("androidx.hilt:hilt-navigation-compose:1.2.0") // Check latest version

    //RETROFIT
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // Use the latest version

    //GSON converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // Use the latest version

    //HTTP client and logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Use the latest version compatible with your Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional, for logging requests/responses
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
    //MOCK Webserver
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.dagger:hilt-android-testing:2.52")
    kaptTest("com.google.dagger:hilt-compiler:2.52")
    testImplementation("org.mockito:mockito-inline:5.2.0")



    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Coroutines test utilities (for testing suspend functions)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")

    // Hilt testing
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.52")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.52")
    // Mockito core (required)
//    androidTestImplementation("org.mockito:mockito-core:5.12.0")
    androidTestImplementation("org.mockito:mockito-android:5.12.0")
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
// Mockito-Kotlin extension (adds better Kotlin syntax & null-safety)
    androidTestImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    androidTestImplementation("androidx.arch.core:core-testing:2.2.0")
//    androidTestImplementation("io.mockk:mockk-android:1.13.10")

}