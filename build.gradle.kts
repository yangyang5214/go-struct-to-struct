plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()


repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}


//https://plugins.jetbrains.com/docs/intellij/goland.html#pluginxml
dependencies {
    intellijPlatform {
//        goland("2024.2.3")
        local("/Users/beer/Applications/GoLand.app/Contents")

        bundledPlugin("org.jetbrains.plugins.go")
        zipSigner()
        instrumentationTools()
    }
}



tasks {
    patchPluginXml {
        sinceBuild.set("222")
    }
}