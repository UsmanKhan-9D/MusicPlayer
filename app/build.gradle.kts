plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.todokanai.musicplayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.todokanai.musicplayer"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.4"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    val roomVersion = "2.6.1"
    val glideVersion = "4.13.0"

    implementation(composeBom)
    testImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.7.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.material:material:1.6.1")
    implementation ("androidx.fragment:fragment-ktx:1.7.1")

    implementation ("androidx.room:room-runtime:$roomVersion")
    kapt ("androidx.room:room-compiler:$roomVersion")
    implementation ("androidx.room:room-ktx:$roomVersion")

    implementation ("androidx.legacy:legacy-support-v4:1.0.0")
    implementation ("androidx.compose.ui:ui:1.5.0-alpha04")
    implementation ("androidx.compose.material3:material3:1.1.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.3")

    implementation ("com.jakewharton.timber:timber:4.7.1")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation ("androidx.navigation:navigation-compose:2.8.0-alpha08")
    implementation ("io.coil-kt:coil-compose:2.1.0")

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    implementation ("com.github.bumptech.glide:glide:$glideVersion")
    annotationProcessor ("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation ("androidx.datastore:datastore-preferences:1.1.0-alpha01")
}