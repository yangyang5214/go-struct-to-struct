plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.14.1"
}

group = "com.yangyang5214"
version = "0.0.1"

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
//    localPath.set("/Users/beer./Library/Application Support/JetBrains/Toolbox/apps/Goland/ch-0/222.4345.24/GoLand.app/Contents")
    localPath.set("/Users/beer/Library/Application Support/JetBrains/Toolbox/apps/Goland/ch-0/231.9011.34/GoLand.app/Contents")
    // https://plugins.jetbrains.com/docs/intellij/goland.html#plugin-and-module-dependencies
    plugins.set(listOf("org.jetbrains.plugins.go"))
}

tasks {

    buildSearchableOptions {
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("222")
        untilBuild.set("232.*")
    }
}
