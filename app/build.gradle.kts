plugins {
    id("com.android.application")
}

android {
    namespace = "com.unasp.atmosweatherapp" // Nome do pacote
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unasp.atmosweatherapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    // Bibliotecas para desenvolvimento Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Retrofit para requisições HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Converter JSON automaticamente
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Biblioteca Gson para manipulação de JSON
    implementation("com.google.code.gson:gson:2.8.9")

    // Testes
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
