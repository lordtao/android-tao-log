import java.util.Date

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

val libName = "taolog"

val skipCommitsCount = 0
val versionMajor = 2
val versionMinor = 2
val versionPatch = providers
    .exec {
        commandLine("git", "rev-list", "--count", "HEAD")
    }.standardOutput.asText
    .get()
    .trim()
    .toInt()

val versionName = "${versionMajor}.${versionMinor}.${versionPatch - skipCommitsCount}"

fun TaskContainer.registerCopyAarTask(variant: String) {
    val capVariant = variant.replaceFirstChar { it.uppercaseChar() }
    register<Delete>("deleteOld${capVariant}Aar") {
        group = "aar"
        description = "Удаляет ранее собранные AAR в ../aar для $variant"
        delete(fileTree("../aar") {
            include("$libName-$variant*.aar")
        })
    }

    register<Copy>("copy${capVariant}Aar") {
        group = "aar"
        description = "Copy AAR $variant with version $versionName to ../aar"
        dependsOn("assemble${capVariant}")
        dependsOn("deleteOld${capVariant}Aar")
        val aarFile = file("build/outputs/aar/$libName-$versionName-$variant.aar")
        doFirst {
            // Создать ../aar если не существует
            file("../aar").mkdirs()
            if (!aarFile.exists()) {
                throw GradleException("AAR file does not exist: $aarFile")
            }
            println("Copying $aarFile to ../aar/$libName-$variant.aar")
        }
        from(aarFile)
        into("../aar")
        if (variant == "release") {
            rename { "$libName.aar" }
        } else {
            rename { "$libName-$variant.aar" }
        }
        doLast {
            val versionFile = file("../aar/README.txt")
            versionFile.writeText("Library: $libName\nVersion: $versionName\nCreated: ${Date()}")
            println("Created version file: ${versionFile.absolutePath}")
        }
    }
}

tasks.registerCopyAarTask("release")
tasks.registerCopyAarTask("debug")

android {
    namespace = "ua.at.tsvetkov.util.logger"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        setProperty("archivesBaseName", "$libName-$versionName")
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.txt")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures.viewBinding = true
    buildFeatures.buildConfig = true
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.cardview)
    implementation(libs.google.material)
    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
}


afterEvaluate {

    tasks.named("assembleDebug").configure {
        finalizedBy("copyDebugAar")
    }
    tasks.named("assembleRelease").configure {
        finalizedBy("copyReleaseAar")
    }
    tasks.named("build").configure {
        dependsOn("copyReleaseAar")
        dependsOn("copyDebugAar")
    }

    // Publishing

    val libGroupId = "ua.at.tsvetkov"
    val libArtifactId = libName
    val libVersionName = versionName

    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = libGroupId
                artifactId = libArtifactId
                version = libVersionName

                from(components.getByName("release"))

                pom {
                    name.set(libArtifactId)
                    description.set("Tiny, lightweight and informative logger for Android.") // Ваше описание
                    url.set("https://github.com/lordtao/android-tao-log") // URL проекта

                    licenses {
                        license {
                            name.set("The MIT License") // Пример
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("lordtao")
                            name.set("Alexandr Tsvetkov")
                            email.set("tsvetkov2010@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/lordtao/android-tao-log.git")
                        developerConnection.set("scm:git:ssh://github.com/lordtao/android-tao-log.git")
                        url.set("https://github.com/lordtao/android-tao-log")
                    }
                }
            }
        }
    }
}
