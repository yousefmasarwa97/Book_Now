plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.myapp.booknow"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.myapp.booknow"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))// This dependency is used to manage Firebase dependencies in the Android project by specifying the Firebase Bill of Materials (BOM).


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")// This dependency is used to analyse the usage of firebase project by the users.


    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    //Firebase Authentication
    implementation("com.google.firebase:firebase-auth")// This dependency is used for users authentication and security.

    //dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")// This dependency is used to enable users use the data base (upload/get) data to/from database.



    constraints {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.0") {
            because("kotlin-stdlib-jdk7 is now a part of kotlin-stdlib")
        }
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0") {
            because("kotlin-stdlib-jdk8 is now a part of kotlin-stdlib")
        }
    }

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("androidx.navigation:navigation-fragment:2.6.0")
    implementation("androidx.navigation:navigation-ui:2.6.0")
    implementation("androidx.activity:activity:1.8.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.github.bumptech.glide:glide:4.12.0")// This dependency is used to enable media management and image loading.
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")// ....
    implementation("com.google.firebase:firebase-storage")// This dependency is used to enable the users use firebase storage to store data such as images.
    implementation("com.google.android.material:material:1.1.0")// Not the latest version ,, this dependency is used for design materials (GUI).
    implementation("com.hbb20:ccp:2.5.2")// Not the latest version ,, this dependency is used to enable using the country code picker in the app.
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.30") // The latest libphonenumber version (Release) ,, this dependency is used to check whether a ph number is valid or not.
    implementation("io.github.chaosleung:pinview:1.4.4")// (check the version),, This dependency is used for OTP PIN view design
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")



}