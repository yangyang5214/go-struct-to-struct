plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.1"
}

group = "com.yangyang5214"
version = "0.0.6"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    type.set("GO")
//    version.set("2022.2.5")

    // local develop idea location
//    localPath.set("/Users/beer./Library/Application Support/JetBrains/Toolbox/apps/Goland/ch-0/222.4345.24/GoLand.app/Contents")
    localPath.set("/Users/beer/Library/Application Support/JetBrains/Toolbox/apps/Goland/ch-0/233.13135.104/GoLand.app/Contents")

    // https://plugins.jetbrains.com/docs/intellij/goland.html#plugin-and-module-dependencies
    plugins.set(listOf("org.jetbrains.plugins.go"))
}

tasks {

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("222")
        // https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html#platformVersions
        untilBuild.set("233.*")
    }
}
