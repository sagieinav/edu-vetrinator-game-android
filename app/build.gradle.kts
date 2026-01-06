import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.sagi.georgethevetrinator"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "dev.sagi.georgethevetrinator"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val propertiesFile = project.rootProject.file("local.properties")
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
        manifestPlaceholders["MAPS_API_KEY"] = properties.getProperty("MAPS_API_KEY")
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

}

dependencies {
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}