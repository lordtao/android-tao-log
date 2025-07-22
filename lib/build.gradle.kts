import java.util.Date

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

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
            include("taolog-$variant-*.aar")
        })
    }

    register<Copy>("copy${capVariant}Aar") {
        group = "aar"
        description = "Copy AAR $variant with version $versionName to ../aar"
        dependsOn("assemble${capVariant}")
        dependsOn("deleteOld${capVariant}Aar")
        val aarFile = file("build/outputs/aar/taolog-$versionName-$variant.aar")
        doFirst {
            // Создать ../aar если не существует
            file("../aar").mkdirs()
            if (!aarFile.exists()) {
                throw GradleException("AAR file does not exist: $aarFile")
            }
            println("Copying $aarFile to ../aar/taolog-$variant.aar")
        }
        from(aarFile)
        into("../aar")
        if(variant == "release") {
            rename { "taolog.aar" }
        } else {
            rename { "taolog-$variant.aar" }
        }
        doLast {
            val versionFile = file("../aar/README.txt")
            versionFile.writeText("Library: taolog\nVersion: $versionName\nCreated: ${Date()}")
            println("Created version file: ${versionFile.absolutePath}")
        }
    }
}

tasks.registerCopyAarTask("release")
tasks.registerCopyAarTask("debug")

android {
    namespace = "ua.at.tsvetkov.util"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        setProperty("archivesBaseName", "taolog-$versionName")
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
}

// Publishing
//
//apply plugin: 'com.jfrog.bintray'
//apply plugin: 'com.github.dcendents.android-maven'
//
//def AUTHOR = "Alexandr Tsvetkov"
//def LIB_DESCRIPTION = 'Tiny, lightweight and informative logger for Android.'
//def GROUP_ID = 'ua.at.tsvetkov'
//def ARTIFACT_ID = 'taolog'
//def LICENSE = 'MIT'
//def LICENSE_URL = 'https://opensource.org/license/mit'
//def GIT_USER = 'lordtao'
//def GIT_PROJECT_NAME = 'android-tao-log'
//def GIT_SITE_URL = 'https://github.com/lordtao/android-tao-log'
//def GIT_ISSUE_URL = 'https://github.com/lordtao/android-tao-log/issues'
//def GIT_URL = 'https://github.com/lordtao/android-tao-log.git'
//def EMAIL = 'tsvetkov2010@gmail.com'
//
//// Hide properties from ext users
//Properties properties = new Properties()
//properties.load(project.rootProject.file('local.properties').newDataInputStream())
//
//bintray {
//    user = properties.getProperty("bintray.user")
//    key = properties.getProperty("bintray.apikey")
//    configurations = ["archives"]
//    pkg {
//        repo = "maven"
//        name = GIT_PROJECT_NAME
//        websiteUrl = GIT_SITE_URL
//        vcsUrl = GIT_URL
//        licenses = listOf(LICENSE)
//        publish = true
//    }
//}
//
//install {
//    repositories.mavenInstaller {
//        pom {
//            project {
//                packaging = "aar"
//                name = LIB_DESCRIPTION
//                url = GIT_SITE_URL
//
//                licenses {
//                    license {
//                        name = LICENSE
//                        url = LICENSE_URL
//                    }
//                }
//                developers {
//                    developer {
//                        id = GIT_USER
//                        name = AUTHOR
//                        email = EMAIL
//                    }
//                }
//                scm {
//                    connection = GIT_URL
//                    developerConnection = GIT_URL
//                    url = GIT_SITE_URL
//                }
//            }
//        }
//    }
//}
//
//task<Jar>("sourcesJar") {
//    from(android.sourceSets["main"].java.srcDirs)
//    archiveClassifier.set("sources")
//}
//
//task<Javadoc>("javadoc") {
//    source = android.sourceSets["main"].java.srcDirs
//    classpath += project.files(android.getBootClasspath().joinToString(File.pathSeparator))
//}
//
//task<Jar>("javadocJar") {
//    dependsOn("javadoc")
//    archiveClassifier.set("javadoc")
//    from(javadoc.get().destinationDir)
//}
//
//artifacts {
//    archives(javadocJar)
//    archives(sourcesJar)
//}
//
//task("findConventions") {
//    doLast {
//        println(project.getConvention())
//    }
//}
