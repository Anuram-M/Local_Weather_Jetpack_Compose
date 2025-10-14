plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")//added for hilt dependency
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

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    implementation("com.google.dagger:hilt-android:2.51.1") // Check latest version
    kapt("com.google.dagger:hilt-compiler:2.51.1")

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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}