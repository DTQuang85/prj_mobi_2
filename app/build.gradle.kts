plugins {
    id("com.android.application")
    id("com.google.gms.google-services") // dùng Firebase
}

android {
    namespace = "com.example.app_ban_hang"
    compileSdk = 36 // hoặc 35/36 nếu SDK đã cài đầy đủ

    defaultConfig {
        applicationId = "com.example.app_ban_hang"
        minSdk = 24
        targetSdk = 36 // hoặc 35/36 tùy bạn đã cài API
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // AGP 8.x yêu cầu JDK >=17. Bạn có JDK 21 vẫn OK với AGP 8.13.0
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Nếu bạn dùng Version Catalog (libs.*) thì giữ 4 dòng dưới.
    // Nếu không có catalogs, thay bằng phiên bản cụ thể.
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)


    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.11.0")

    // Firebase qua BoM (không ghi version cho từng sdk)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("androidx.browser:browser:1.8.0")
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")
}
