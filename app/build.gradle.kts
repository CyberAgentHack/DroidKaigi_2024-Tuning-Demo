plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.hilt)
  alias(libs.plugins.ksp)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "com.example.album"
  compileSdk = libs.versions.compileSdk.get().toInt()
  buildToolsVersion = libs.versions.buildTools.get()

  defaultConfig {
    applicationId = "com.example.album"
    minSdk = libs.versions.minSdk.get().toInt()
    targetSdk = libs.versions.targetSdk.get().toInt()
    versionCode = libs.versions.versionCode.get().toInt()
    versionName = libs.versions.versionName.get()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      manifestPlaceholders["app_name"] = "Album(Debug)"
      applicationIdSuffix = ".debug"
      isDebuggable = true
      isMinifyEnabled = false
    }

    release {
      manifestPlaceholders["app_name"] = "Album"
      isDebuggable = false
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  signingConfigs {
    create("release") {
      storeFile = file("~/.android/debug.keystore") // for mac OS
//      storeFile = file("C://Users/$user/.android/debug.keystore") // for windows OS
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildFeatures {
    compose = true
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlin {
    jvmToolchain(17)
  }
}

composeCompiler {
  reportsDestination = layout.buildDirectory.dir("compose_compiler")
  metricsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  implementation(libs.androidx.compose.runtime)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.hilt.navigation.compose)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.uiTooling)
  implementation(libs.androidx.lifecycle.runtime.compose)

  implementation(libs.coil.core)
  implementation(libs.coil.compose)

  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  implementation(libs.retrofit)
  implementation(libs.okhttp)
  implementation(libs.kotlin.serialization.json)
  implementation(libs.retrofit.converter.serialization)

  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  implementation(libs.hilt.android)
  ksp(libs.hilt.compiler)

  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.collections.immutable)

  implementation(libs.timber)
}
