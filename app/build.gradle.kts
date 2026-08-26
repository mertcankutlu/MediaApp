plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.media.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.media.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core & Compose UI
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.material3)

    // Media3 (Oynatıcı ve Servis - Playback Persistent)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)

    // Room (Tek Gerçeklik Kaynağı - Room Authoritative)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // WorkManager (İndirmeler - Downloads Durable)
    implementation(libs.work.runtime.ktx)

    // Hilt (Bağımlılık Enjeksiyonu)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Ağ ve Görsel İşlemleri (Network Replaceable)
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.coil.compose)

    // yt-dlp (İzole Çözümleyici - yt-dlp Exceptional)
    implementation(libs.youtubedl.android)
}
