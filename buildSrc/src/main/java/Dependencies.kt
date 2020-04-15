package dependencies

/**
 * Created by eduardo.delito on 3/3/20.
 */
// Object that is used for dependency version
object Versions {

    //version
    const val versionCode = 1
    const val versionName = "1.0"

    // build
    const val minSdk = 23
    const val compileSdk = 29
    const val targetSdk = 29
    const val buildTools = "29.0.2"

    // kotlin
    const val kotlin = "1.3.61"

    // android
    const val appcompat = "1.1.0"
    const val androidGradle = "3.5.3"
    const val constraintLayout = "1.1.3"
    const val lifecycle = "2.1.0"
    const val coreKtx = "1.0.0"
    const val room = "2.2.3"
    const val multidex = "1.0.3"
    const val navigation = "2.1.0"
    const val legacySupport = "1.0.0"

    // google
    const val dagger = "2.26"
    const val materialDesign = "1.2.0-alpha03"

    // test
    const val testRunner = "1.2.0"
    const val androidxTest = "3.2.0"
    const val jUnit = "4.12"
    const val mockito = "2.24.0"

    // third parties
    const val rxJavaVersion = "2.2.12"
    const val rxAndroidVersion = "2.1.1"

    // network
    const val retrofit = "2.5.0"
    const val fresco = "2.0.0"
    const val picaso = "2.71828"
}

// Object that contains libraries needed by the app
object Libs {

    private object Kotlin {
        const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
    }

    private object Android {
        const val androidGradle = "com.android.tools.build:gradle:${Versions.androidGradle}"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

        const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
        const val lifecycleViewModelktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
        const val lifecycleCompiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"

        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val materialDesign = "com.google.android.material:material:${Versions.materialDesign}"

        const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
        const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
        const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
        const val roomRxJava = "androidx.room:room-rxjava2:${Versions.room}"
        const val roomGuava = "androidx.room:room-guava:${Versions.room}"
        const val roomTesting = "androidx.room:room-testing:${Versions.room}"

        const val multiDexSupport = "com.android.support:multidex:${Versions.multidex}"
        const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    }

    private object Dagger {
        const val core = "com.google.dagger:dagger:${Versions.dagger}"
        const val android = "com.google.dagger:dagger-android:${Versions.dagger}"
        const val androidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val processor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
        const val compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    }

    // Section for third-party dependencies
    private object Network {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val retrofitGSONConverter = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
        const val retrofitRxAdapter = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    }

    private object RxJava {
        const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJavaVersion}"
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroidVersion}"
    }

    private object Test {
        const val jUnit = "junit:junit:${Versions.jUnit}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val androidxTestCore = "androidx.test.espresso:espresso-core:${Versions.androidxTest}"
    }
    // End of section for third-party dependencies
}