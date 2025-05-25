plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.sddrozdov.doska"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sddrozdov.doska"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Firebase
    implementation(platform(libs.firebase.bom)) // Firebase BOM for version management
    implementation(libs.firebase.auth)           // Firebase Authentication
    implementation(libs.firebase.database)       // Firebase Realtime Database
    implementation(libs.firebase.storage)        // Firebase Storage

    // Google Auth
    implementation(libs.androidx.credentials)                     // AndroidX Credentials Library
    implementation(libs.androidx.credentials.play.services.auth)  // Play Services Auth for Credentials
    implementation(libs.googleid)                                 // Google ID Library

    // Core Libraries
    implementation(libs.androidx.core.ktx)                        // AndroidX Core KTX
    implementation(libs.androidx.appcompat)                       // AndroidX AppCompat
    implementation(libs.material)                                  // Material Components
    implementation(libs.androidx.activity)                        // AndroidX Activity
    implementation(libs.androidx.constraintlayout)               // Constraint Layout
    implementation(libs.androidx.exifinterface)                  // Exif Interface for image metadata

    // Room (uncomment if using Room)
    //ksp(libs.androidx.room.compiler)                            // Room Compiler for annotation processing

    // Testing Libraries
    testImplementation(libs.junit)                                 // JUnit for unit testing
    androidTestImplementation(libs.androidx.junit)               // AndroidX JUnit for Android testing
    androidTestImplementation(libs.androidx.espresso.core)       // Espresso Core for UI testing
    androidTestImplementation(libs.androidx.espresso.contrib)    // Espresso Contrib for additional testing features
    testImplementation(libs.mockito.core)                         // Mockito for mocking in tests
    testImplementation(libs.mockito.inline)                       // Inline Mockito for testing
    androidTestImplementation(libs.androidx.core)                 // AndroidX Core for testing with Android context

    // Coroutine Libraries
    // Uncomment if using ViewModel and Lifecycle with Coroutines
    //implementation(libs.androidx.lifecycle.viewmodel.ktx)
    //implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.kotlinx.coroutines.core)                  // Kotlin Coroutines Core
    implementation(libs.kotlinx.coroutines.android)               // Kotlin Coroutines for Android

    // Image Handling
    implementation(libs.piximagepicker)                           // Image Picker Library
    implementation(libs.picasso)                                  // Picasso for image loading
    implementation(libs.circleimageview)                          // Circle Image View for circular images
    implementation(libs.androidx.recyclerview)                    // RecyclerView for displaying lists
    implementation(libs.glide)                                    // Glide for image loading and caching

    // Test Advertising (uncomment if needed)
    // implementation(libs.mytarget.sdk)                           // MyTarget SDK for advertising

    // Appwrite SDK
    implementation("io.appwrite:sdk-for-android:7.0.0")          // Appwrite SDK for backend services
}
