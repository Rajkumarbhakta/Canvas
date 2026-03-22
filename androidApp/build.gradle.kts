import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.rkbapps.canvas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rkbapps.canvas"
        minSdk = 23
        targetSdk = 36
        versionCode = 3
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        debug {
        }
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}



dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activityCompose)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)



    implementation(libs.kotlinx.coroutines.android)
    //koin dependency injection
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.androidx.core.ktx)


}
