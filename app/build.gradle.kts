
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}




android {

        packagingOptions {
            resources {
                excludes += "META-INF/DEPENDENCIES"
            }
        }





    namespace = "com.example.matchmaker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.matchmaker"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled= true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }




    buildTypes {
        release {



            isMinifyEnabled = true
            android.buildFeatures.buildConfig=true



            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            val properties = Properties()
            properties.load(project.rootProject.file("local.properties").inputStream())
            buildConfigField("String", "FIREBASE_API_KEY", properties.getProperty("FIREBASE_API_KEY"))
            buildConfigField("String", "GCM_DEFAULT_SENDER_ID", properties.getProperty("GCM_DEFAULT_SENDER_ID"))
            buildConfigField("String", "GOOGLE_APP_ID", properties.getProperty("GOOGLE_APP_ID"))


            signingConfig = signingConfigs.create("release") {
                storeFile = file(properties.getProperty("KEYSTORE_PATH"))
                storePassword = properties.getProperty("KEYSTORE_PASSWORD")
                keyAlias = properties.getProperty("KEY_ALIAS")
                keyPassword = properties.getProperty("KEY_PASSWORD")
            }

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation ("androidx.compose.material:material:1.5.0")
    implementation ("androidx.compose.material:material-icons-core:1.5.0")
    implementation ("androidx.compose.material:material-icons-extended:1.5.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("com.google.firebase:firebase-database:20.0.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.0")
    implementation("com.google.firebase:firebase-analytics")

    // Declare the KTX module instead (which automatically has a dependency on the main module)
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.10.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation ("com.google.firebase:firebase-storage-ktx:20.3.0")




    //youtube
    implementation ("com.google.api-client:google-api-client-android:1.35.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev222-1.25.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation ("androidx.compose.ui:ui:1.5.0")
    implementation ("androidx.compose.material3:material3:1.1.1")
    implementation ("androidx.navigation:navigation-compose:2.5.3")
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.google.http-client:google-http-client-gson:1.42.3")
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation ("com.google.android.exoplayer:exoplayer:2.18.1")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:11.1.0")
    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("com.google.accompanist:accompanist-pager:0.31.3-beta") // ✅ Latest Pager
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.31.3-beta")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")
    implementation ("com.cloudinary:cloudinary-android:2.3.1")
    implementation("com.google.android.gms:play-services-ads:23.6.0") // If you wish to use full SDK features
    implementation("com.google.android.gms:play-services-ads-identifier:17.0.0") // For Ad tracking (if needed)




    configurations.all {
        resolutionStrategy {
            force ("com.google.android.gms:play-services-auth:20.6.0")
            force ("com.google.android.gms:play-services-base:18.2.0")
        }
    }




    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}





