// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.jetbrainsKotlinJvm) apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
    id("com.vanniktech.maven.publish") version "0.25.2" apply false
//    id("tech.joechen.funcost") version "0.0.1" apply false
}


tasks.register("publishFunCost") {
    group = "Publishing"
    description = "Publishes all the FunCost libraries to the Maven repository."

    dependsOn(
        ":funcost-annotations:publish",
        ":funcost-compiler:publish"
    )
}