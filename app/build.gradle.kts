plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.proyectointegrador"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectointegrador"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    // Dependencias de la aplicación
    implementation(libs.appcompat) // Biblioteca de Android Support
    implementation(libs.material) // Biblioteca de Material Design
    implementation(libs.activity) // Biblioteca de Activity
    implementation(libs.constraintlayout) // Biblioteca de Layout de Restricciones
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Para manejar imágenes
    implementation(libs.picasso) // Biblioteca de manejo de imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Circle Image View
    implementation(libs.circleimageview) // Biblioteca de vista de imagen circular

    // Shape Of View
    implementation(libs.shapeofview)
    implementation(libs.cardview) // Biblioteca de vista de forma

    // Dependencias de pruebas
    testImplementation(libs.junit) // Biblioteca de pruebas de JUnit
    androidTestImplementation(libs.ext.junit) // Biblioteca de pruebas de JUnit para Android
    androidTestImplementation(libs.espresso.core) // Biblioteca de pruebas de Espresso

    // Parse SDK
    implementation(libs.bolts.tasks)
    implementation(libs.parse)
    implementation("com.github.parse-community:ParseLiveQuery-Android:1.2.2")         // Consultas en tiempo real
    implementation("com.github.parse-community.Parse-SDK-Android:fcm:4.3.0")          // Integración con Firebase Cloud Messaging
}
