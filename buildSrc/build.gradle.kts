import dev.kikugie.stonecutter.StonecutterBuild

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("me.modmuss50.mod-publish-plugin") version "0.6.3" apply false
}

gradlePlugin {
    plugins {
        register("txnitemplate-plugin") {
            id = "txnitemplate"
            implementationClass = "TxniTemplatePlugin"
        }
    }
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.architectury.dev")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.kikugie.dev/snapshots")
    maven("https://maven.kikugie.dev/releases")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
}

dependencies {
    implementation("me.modmuss50:mod-publish-plugin:0.6.3")
    implementation("dev.architectury.loom:dev.architectury.loom.gradle.plugin:1.7-SNAPSHOT")

    implementation("dev.kikugie:stonecutter:0.5-alpha.4")
}