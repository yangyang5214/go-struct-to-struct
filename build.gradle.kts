plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
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
    version.set("2022.2.5")
    type.set("IC") // Target IDE Platform
//    type.set("IU") // Target IDE Platform
//    type.set("GO") // Target IDE Platform
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
