import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
plugins {
    id("com.android.application")
}

android {
    namespace = "com.common.voltnfcandroid"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.common.voltnfcandroid"
        minSdk = 23
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    android.applicationVariants.all {
        outputs.all {
            if (this is ApkVariantOutputImpl) {
                val config = project.android.defaultConfig
                val versionName = config.versionName
                val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
                val createTime =LocalDateTime.now().format(formatter)
                this.outputFileName = "volt-nfc-android-V${versionName}-$createTime.apk"
            }
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.github.JessYanCoding:AndroidAutoSize:v1.2.1")
}