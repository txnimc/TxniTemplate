val settings = object : TxniTemplateSettings {

	// -------------------- Dependencies ---------------------- //
	override val depsHandler: DependencyHandler get() = object : DependencyHandler {
		override fun addGlobal(deps: DependencyHandlerScope) {

		}

		override fun addFabric(deps: DependencyHandlerScope) {

		}

		override fun addForge(deps: DependencyHandlerScope) {

		}

		override fun addNeo(deps: DependencyHandlerScope) {

		}
	}


	// ---------- Curseforge/Modrinth Configuration ----------- //
	// For configuring the dependecies that will show up on your mod page.
	override val publishHandler: PublishDependencyHandler get() = object : PublishDependencyHandler {
		override fun addShared(deps: DependencyContainer) {
			if (mod.isFabric) {
				deps.requires("fabric-api")
			}
		}

		override fun addCurseForge(deps: DependencyContainer) {

		}

		override fun addModrinth(deps: DependencyContainer) {

		}
	}
}


// ---------------TxniTemplate Build Script---------------- //
//   (only edit below this if you know what you're doing)
// -------------------------------------------------------- //

plugins {
	`maven-publish`
	txnitemplate
	application
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("dev.kikugie.j52j") version "1.0"
	id("dev.architectury.loom")
	id("me.modmuss50.mod-publish-plugin")
	id("systems.manifold.manifold-gradle-plugin")
}

// The manifold Gradle plugin version. Update this if you update your IntelliJ Plugin!
manifold { manifoldVersion = "2024.1.31" }

txnitemplate {
	sc = stonecutter
	init()
}

val mod = txnitemplate.mod



// Dependencies
repositories {
	exclusiveMaven("https://www.cursemaven.com", "curse.maven")
	exclusiveMaven("https://api.modrinth.com/maven", "maven.modrinth")
	exclusiveMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
	maven("https://maven.kikugie.dev/releases")
	maven("https://jitpack.io")
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
	maven("https://maven.parchmentmc.org")
	maven("https://maven.su5ed.dev/releases")
}

dependencies {
	// apply the Manifold processor, do not remove this unless you want to swap back to Stonecutter preprocessor
	implementation(annotationProcessor("systems.manifold:manifold-preprocessor:${manifold.manifoldVersion.get()}")!!)

	compileOnly("org.projectlombok:lombok:1.18.34")
	annotationProcessor("org.projectlombok:lombok:1.18.34")

	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
		val parchmentVersion = when (mod.mcVersion) {
			"1.18.2" -> "1.18.2:2022.11.06"
			"1.19.2" -> "1.19.2:2022.11.27"
			"1.20.1" -> "1.20.1:2023.09.03"
			"1.21.1" -> "1.21:2024.07.28"
			else -> ""
		}
		parchment("org.parchmentmc.data:parchment-$parchmentVersion@zip")
	})

	settings.depsHandler.addGlobal(this)

	if (mod.isFabric) {
		modImplementation(settings.depsHandler.modrinth("modmenu", property("deps.modmenu")))

		settings.depsHandler.addFabric(this)
		modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

		if (setting("runtime.sodium"))
			modRuntimeOnly(settings.depsHandler.modrinth("sodium", when (mod.mcVersion) {
				"1.21.1" -> "mc1.21-0.6.0-beta.1-fabric"
				"1.20.1" -> "mc1.20.1-0.5.11"
				else -> null
			}))
	}

	if (mod.isForge) {
		settings.depsHandler.addForge(this)
		"forge"("net.minecraftforge:forge:${mod.mcVersion}-${property("deps.fml")}")
	}

	if (mod.isNeo) {
		settings.depsHandler.addNeo(this)
		"neoForge"("net.neoforged:neoforge:${property("deps.fml")}")

		if (setting("runtime.sodium"))
			runtimeOnly(settings.depsHandler.modrinth("sodium", "mc1.21-0.6.0-beta.1-neoforge"))
	}

	vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.1")
}

fun setting(prop : String) : Boolean = property(prop) == "true"

// Loom config
loom {
	val awFile = rootProject.file("src/main/resources/${mod.id}.accesswidener")
	if (awFile.exists())
		accessWidenerPath.set(awFile)

	if (mod.loader == "forge") forge {
		convertAccessWideners.set(true)
		mixinConfigs("mixins.${mod.id}.json")
	}

	if (mod.isActive) {
		runConfigs.all {
			ideConfigGenerated(true)
			vmArgs("-Dmixin.debug.export=true", "-Dsodium.checks.issue2561=false")
			// Mom look I'm in the codebase!
			programArgs("--username=${mod.clientuser}", "--uuid=${mod.clientuuid}")
			runDir = "../../run/${stonecutter.current.project}/"
		}
	}

	decompilers {
		get("vineflower").apply {
			options.put("mark-corresponding-synthetics", "1")
		}
	}

	runs {
		register("datagen") {
			client()
			name("DataGen Client")
			vmArg("-Dfabric-api.datagen")
			vmArg("-Dfabric-api.datagen.output-dir=" + getRootDir().toPath().resolve("src/main/generated"))
			vmArg("-Dfabric-api.datagen.modid=${mod.id}")
			ideConfigGenerated(false)
			runDir("build/datagen")
		}
	}
}

sourceSets {
	main {
		resources {
			srcDir("src/main/generated")
			exclude(".cache/")
		}
	}
}

// Tasks
tasks {
	remapJar {
		if (mod.isNeo) {
			atAccessWideners.add("${mod.id}.accesswidener")
		}
	}
}

tasks.compileJava {
	options.encoding = "UTF-8"
	options.compilerArgs.add("-Xplugin:Manifold")
	// modify the JavaCompile task and inject our auto-generated Manifold symbols
	doFirst {
		if(!this.name.startsWith("_")) { // check the name, so we don't inject into Forge internal compilation
			ManifoldMC.setupPreprocessor(options.compilerArgs, mod.loader, projectDir, mod.mcVersion, stonecutter.active.project == stonecutter.current.project, false)
		}
	}
}

project.tasks.register("setupManifoldPreprocessors") {
	group = "build"
	doLast {
		ManifoldMC.setupPreprocessor(ArrayList(), mod.loader, projectDir, mod.mcVersion, stonecutter.active.project == stonecutter.current.project, true)
	}
}

tasks.setupChiseledBuild { finalizedBy("setupManifoldPreprocessors") }

tasks.register<RenameExampleMod>("renameExampleMod", rootDir, mod.id, mod.name, mod.displayName, mod.namespace, mod.group).configure {
	group = "build helpers"
	description = "Renames the example mod to match the mod ID, name, and display name in gradle.properties"
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.remapJar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}

if (stonecutter.current.isActive) {
	rootProject.tasks.register("buildActive") {
		group = "project"
		dependsOn(buildAndCollect)
	}

	rootProject.tasks.register("runActive") {
		group = "project"
		dependsOn(tasks.named("runClient"))
	}
}

stonecutter {
	val j21 = eval(mod.mcVersion, ">=1.20.6")
	java {
		withSourcesJar()
		sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
		targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	}

	kotlin {
		jvmToolchain(if (j21) 21 else 17)
	}
}

tasks.processResources {
	val map = mapOf(
		"version" to mod.version,
		"mc" to mod.mcDep,
		"id" to mod.id,
		"group" to mod.group,
		"author" to mod.author,
		"namespace" to mod.namespace,
		"description" to mod.description,
		"name" to mod.name,
		"license" to mod.license,
		"github" to mod.github,
		"display_name" to mod.displayName,
		"fml" to if (mod.loader == "neoforge") "1" else "45",
		"mnd" to if (mod.loader == "neoforge") "" else "mandatory = true"
	)

	filesMatching("fabric.mod.json") { expand(map) }
	filesMatching("META-INF/mods.toml") { expand(map) }
	filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

// Publishing
publishMods {
	file = tasks.remapJar.get().archiveFile
	additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
	displayName = "${mod.name} ${mod.loader.replaceFirstChar { it.uppercase() }} ${mod.version} for ${property("mod.mc_title")}"
	version = mod.version
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.add(mod.loader)

	val targets = property("mod.mc_targets").toString().split(' ')

	dryRun = providers.environmentVariable("MODRINTH_TOKEN").getOrNull() == null ||
			providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

	modrinth {
		projectId = property("publish.modrinth").toString()
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		targets.forEach(minecraftVersions::add)
		val deps = DependencyContainer(null, this)
 		settings.publishHandler.addModrinth(deps)
		settings.publishHandler.addShared(deps)
	}

	curseforge {
		projectId = property("publish.curseforge").toString()
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		targets.forEach(minecraftVersions::add)
		val deps = DependencyContainer(this, null)
		settings.publishHandler.addCurseForge(deps)
		settings.publishHandler.addShared(deps)
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "${property("mod.group")}.${mod.id}"
			version = mod.version
			artifactId = "${mod.loader}-${mod.mcVersion}" //base.archivesName.get()

			from(components["java"])
		}
	}

	repositories {
		val username = "MAVEN_USERNAME".let { System.getenv(it) ?: findProperty(it) }?.toString()
		val password = "MAVEN_PASSWORD".let { System.getenv(it) ?: findProperty(it) }?.toString()

		if (username == null || password == null) {
			println("No maven credentials found.")
            return@repositories;
		}

		val mavenURI = if (properties["publish.use_snapshot_maven"] == "true") "snapshots" else "releases"
		maven {
			name = "${mod.author}_$mavenURI"
			url = uri("https://${property("publish.maven_url").toString()}/$mavenURI")
			credentials {
				this.username = System.getenv("MAVEN_USERNAME")
				this.password = System.getenv("MAVEN_PASSWORD")
			}
		}
	}
}