plugins {
    id("java-gradle-plugin")
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("com.vanniktech.maven.publish")
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation(project(":funcost-annotations"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.5.0")
}



buildConfig {
    packageName("tech.joechen.funcost_compiler")
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${property("KOTLIN_PLUGIN_ID")}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"tech.joechen.funcost\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"funcost-compiler\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${property("FunCostVersion") as String}\"")
}


gradlePlugin {
    plugins {
        create("FunCostGradlePlugin") {
            id = project.properties["KOTLIN_PLUGIN_ID"] as String
            displayName = "FunCost plugin"
            description = "FunCost plugin"
            version = property("FunCostVersion") as String
            implementationClass = "tech.joechen.funcost.compiler.FunCostPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri(rootProject.rootDir.canonicalPath + "/repo")
            group = "tech.joechen.funcost"
            version = property("FunCostVersion") as String
        }
    }
}