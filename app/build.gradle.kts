plugins {
    id("com.android.application")
}

android {
    namespace = "com.diary.superjournalapp"
    compileSdk = 33

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            keyPassword = "Varsha@1029"
            storeFile = file("/home/rushvikallampally/AndroidStudioProjects/SUperJournalApp/DiaryverseApp_jks_file.jks")
            storePassword = "Varsha@1029"

            // Enable/Disable V1 and V2 signing
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = "com.diary.superjournalapp"
        minSdk = 24
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
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.jjoe64:graphview:4.2.2")
    implementation("com.vanniktech:emoji-google:0.6.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    annotationProcessor("androidx.room:room-compiler:2.5.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}