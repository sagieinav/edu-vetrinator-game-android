import org.gradle.kotlin.dsl.support.kotlinCompilerOptions
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

        // Maps API Key
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")

        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        val mapsApiKey: String? = localProperties.getProperty("MAPS_API_KEY")

        if (mapsApiKey == null) {
            logger.error("ERROR: MAPS_API_KEY not found in local.properties! Please add: MAPS_API_KEY={your_key}")
        }
        else {
            manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY")
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
    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.glide)
    implementation(libs.play.services.maps)
    implementation(libs.gson)
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