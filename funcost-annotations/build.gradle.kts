plugins {
    id("java-library")
    alias(libs.plugins.jetbrainsKotlinJvm)
    id("com.github.gmazzo.buildconfig")
    id("com.vanniktech.maven.publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        repositories {
            maven {
                url = uri("../repo")
                group = "tech.joechen.funcost"
                version = property("FunCostVersion") as String
            }
        }
    }
}
