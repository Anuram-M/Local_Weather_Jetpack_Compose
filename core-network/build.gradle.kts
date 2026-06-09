import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}
android {
    namespace = "com.ram.core_network"
    compileSdk = 36

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val weatherKey = localProperties.getProperty("WEATHER_API_KEY") ?: "\"dfdfdf\""

        buildConfigField("String", "WEATHER_API_KEY", weatherKey)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    implementation(project(":core-domain"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.com.google.dagger.hilt.android4)
    ksp(libs.com.google.dagger.hilt.compiler2)

    //RETROFIT
    implementation(libs.retrofit2.retrofit) // Use the latest version

    //GSON converter
    implementation(libs.retrofit2.converter.gson) // Use the latest version

    //HTTP client and logging interceptor
    implementation(libs.okhttp3.okhttp) // Use the latest version compatible with your Retrofit
    implementation(libs.okhttp3.logging.interceptor) // Optional, for logging requests/responses
}