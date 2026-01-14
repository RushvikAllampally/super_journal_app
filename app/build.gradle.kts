plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.diary.superjournalapp"
    compileSdk = 35

    signingConfigs {
        create("release") {
            keyAlias = "key0"
            keyPassword = "Varsha@1029"
            storeFile = file("${rootProject.projectDir}/DiaryverseApp_jks_file.jks")
            storePassword = "Varsha@1029"

            // Enable/Disable V1 and V2 signing
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    defaultConfig {
        applicationId = "com.diary.superjournalapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 13
        versionName = "2.2"

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
        // debug {
        //     // Use the same signing configuration as release
        //     signingConfig = signingConfigs.getByName("release")
        //     isDebuggable = true
        // }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0"
            )
        }
    }
}

dependencies {
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.jjoe64:graphview:4.2.2")
    implementation("com.vanniktech:emoji-google:0.8.0")
    implementation("jp.wasabeef:richeditor-android:2.0.0")
    implementation("com.github.prolificinteractive:material-calendarview:2.0.1")
    
    // PDF Export Library
    implementation("com.itextpdf:itext7-core:7.2.5")
    
    // Google Drive Backup Dependencies
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev20230822-2.0.0")
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation("com.google.http-client:google-http-client-gson:1.44.1")
    implementation("com.google.http-client:google-http-client:1.44.1")
    implementation("androidx.work:work-runtime:2.9.0")
    
    // Security - Certificate Pinning
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Google Play Billing for Premium Subscriptions
    implementation("com.android.billingclient:billing:6.2.1")
    
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}