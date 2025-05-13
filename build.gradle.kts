import org.jetbrains.kotlin.konan.util.defaultTargetSubstitutions

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.appdistribution")
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

android {
    namespace = "com.appoxee.testapp"
    compileSdk = 35
    buildToolsVersion = "35.0.0"

    signingConfigs {
        create("release") {
            storePassword = "mappsdk"
            keyAlias = "mappsdk"
            keyPassword = "mappsdk"
            storeFile = file("engage-release-key")
        }
    }

    defaultConfig {
        applicationId = "com.appoxee.example"
        minSdk = 21
        targetSdk = 35
        versionCode = 22
        versionName = "1.0.58"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )

            // Semsudin test channel on L3
            buildConfigField("String", "SDK_KEY", "\"183408d0cd3632.83592719\"")
            buildConfigField("String", "APP_ID", "\"206974\"")
            buildConfigField("String", "TENANT_ID", "\"5963\"")
            buildConfigField("String", "SERVER_INDEX", "\"L3\"")

            signingConfig = signingConfigs.getByName("release")
        }

        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )

            //signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String", "SDK_KEY", "\"183408d0cd3632.83592719\"")
            buildConfigField("String", "APP_ID", "\"206974\"")
            buildConfigField("String", "TENANT_ID", "\"5963\"")
            buildConfigField("String", "SERVER_INDEX", "\"L3\"")
        }
    }

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "LICENSE.txt"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
        abortOnError = false
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.appcompat:appcompat-resources:1.7.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("com.google.code.gson:gson:2.13.0")
    //implementation(project(":sdk"))
    implementation("com.mapp.sdk:mapp-android:6.0.28-beta01")
    //implementation("com.mapp.sdk:engage-android:7.0.0-beta02")

    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-iid:21.1.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.activity:activity:1.10.1")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
    // implementation("androidx.work:work-runtime:2.7.1")

    androidTestImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:rules:1.6.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation("com.pixplicity.easyprefs:EasyPrefs:1.10.0")
}

// Task registration
tasks.register("publishToTesters") {
    dependsOn("assembleStaging11")
    dependsOn("appDistributionUploadStaging11")
}
