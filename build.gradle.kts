plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
//    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.vanniktechMavenPublish) apply false
}

