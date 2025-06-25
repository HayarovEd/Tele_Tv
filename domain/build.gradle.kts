plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    //date-time
    implementation(libs.kotlinx.datetime)
}