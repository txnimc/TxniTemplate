plugins {
    id ("dev.architectury.loom")
}

repositories {
    exclusiveMaven("https://www.cursemaven.com", "curse.maven")
    exclusiveMaven("https://api.modrinth.com/maven", "maven.modrinth")
    exclusiveMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
    mavenCentral()
    gradlePluginPortal()
}