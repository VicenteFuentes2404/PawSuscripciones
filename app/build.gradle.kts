plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.pawsuscripciones"
    compileSdk = 34 // ⚠️ Cambiado a 34, ya que compileSdk 36 aún no es estable

    defaultConfig {
        applicationId = "com.example.pawsuscripciones"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    // --- Core Android ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // --- Compose BOM (mantiene coherencia de versiones para todo Compose) ---
    // Usar una versión más reciente y estable es recomendable, por ejemplo 2024.05.00
    val composeBom = platform("androidx.compose:compose-bom:2024.05.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material3
    implementation("androidx.compose.material3:material3-window-size-class:1.3.1")

    // --- Jetpack Compose (SIN versiones, el BOM se encarga) ---
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3") // Usa la versión del BOM

    // --- ICONOS: Aquí está la corrección ---
    // Esta librería es necesaria para poder usar `Icons.Default`, `Icons.Filled`, etc.
    implementation("androidx.compose.material:material-icons-core")
    // Esta es para los íconos adicionales (opcional si no los usas, pero bueno tenerla)
    implementation("androidx.compose.material:material-icons-extended")

    // --- Navegación Compose ---
    // Actualizado a la última versión estable (compatible con el BOM 2024.05.00)
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- ViewModel + Lifecycle Compose ---
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")

    // --- Coroutines ---
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // --- Room (Base de datos SQLite) ---
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // --- WorkManager (para tareas en background) ---
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")
    implementation("androidx.compose.material3:material3:1.1.0")

    // --- Accompanist (permisos y extensiones útiles) ---
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")


    // --- Retrofit (Para consumir la API REST) ---
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // --- Gson Converter (Para convertir JSON a objetos Kotlin) ---
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // --- (Opcional) OkHttp Logging Interceptor (Para depurar llamadas de red) ---
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")



    // --- Tests (SIN versiones, el BOM se encarga) ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

