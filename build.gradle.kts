plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}


//https://plugins.jetbrains.com/docs/intellij/goland.html#pluginxml
dependencies {
    intellijPlatform {
       goland("2025.1.1")
       // local("/Users/beer/Applications/GoLand.app")

        bundledPlugin("org.jetbrains.plugins.go")
        zipSigner()
        instrumentationTools()
    }
}



tasks {
    patchPluginXml {
        sinceBuild.set("222")
        untilBuild = provider { null }
    }
}