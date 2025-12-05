plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.homeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.homeapp"
        minSdk = 24
        targetSdk = 36
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
        // Configuraciones para usar Java 11, requerido para compatibilidad con MQTT Paho
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    // Dependencias existentes de AndroidX y Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ------------------------------------------------
    // ⬇️ DEPENDENCIAS PARA MQTT y JSON ⬇️
    // ------------------------------------------------

    // 1. Cliente Paho MQTT (Core)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")

    // 2. Cliente Paho MQTT para Android Service (manejo de conexión en segundo plano)
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    // 3. Gson para serialización/deserialización de mensajes JSON
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Dependencias MQTT existentes:
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    // ------------------------------------------------

    // Dependencias de testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}