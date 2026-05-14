plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.ram.core_network"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

}

dependencies {
    implementation(project(":core-domain"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")

    //RETROFIT
    implementation("com.squareup.retrofit2:retrofit:2.11.0") // Use the latest version

    //GSON converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0") // Use the latest version

    //HTTP client and logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // Use the latest version compatible with your Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional, for logging requests/responses
}