plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// Don't build the demo app on release builds
configure<com.android.build.api.variant.ApplicationAndroidComponentsExtension> {
    beforeVariants(selector().withBuildType("release")) {
        it.enable = false
    }
}

configure<com.android.build.api.dsl.ApplicationExtension> {
    namespace = "ua.at.tsvetkov.demo"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ua.at.tsvetkov.demo"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 2
        versionName = "2.0"
    }

//    signingConfigs {
//        create("release") {
//            keyAlias = "testing"
//            keyPassword = "testing"
//            storePassword = "testing"
//            storeFile = file("keystore/keystore.keystore")
//        }
//    }

    buildTypes {
        getByName("release") {
//            isMinifyEnabled = false
//            isDebuggable = false
//            isShrinkResources = false
//            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(fileTree("libs") { include("*.jar") })
    implementation(project(":logger"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.google.material)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.activity)
}
