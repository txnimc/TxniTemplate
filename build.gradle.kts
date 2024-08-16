import org.jetbrains.kotlin.incremental.createDirectory

plugins {
	`maven-publish`
	kotlin("jvm")
	kotlin("plugin.serialization")
	id("dev.kikugie.j52j") version "1.0"
	id("dev.architectury.loom")
	id("me.modmuss50.mod-publish-plugin")
	id("systems.manifold.manifold-gradle-plugin")
}

// The manifold Gradle plugin version. Update this if you update your IntelliJ Plugin!
manifold { manifoldVersion = "2024.1.30" }

// Variables
class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
	val author = property("mod.author").toString()
	val namespace = property("mod.namespace").toString()
	val displayName = property("mod.display_name").toString()
	val description = property("mod.description").toString()
	val mcDep = property("mod.mc_dep").toString()
	val license = property("mod.license").toString()
	val github = property("mod.github").toString()
}

val mod = ModData()
val mcVersion = stonecutter.current.project.substringBeforeLast('-')

val loader = loom.platform.get().name.lowercase()
val isFabric = loader == "fabric"
val isForge = loader == "forge"
val isNeo = loader == "neoforge"

version = "${mod.version}-$mcVersion"
group = mod.group
base { archivesName.set("${mod.id}-$loader") }

// Dependencies
repositories {
	fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
		forRepository { maven(url) }
		filter { groups.forEach(::includeGroup) }
	}
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth")
	strictMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
	maven("https://maven.kikugie.dev/releases")
	maven("https://jitpack.io")
	maven("https://maven.neoforged.net/releases/")
	maven("https://maven.terraformersmc.com/releases/")
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
}

dependencies {
	fun modrinth(name: String, dep: Any?) = "maven.modrinth:$name:$dep"

	minecraft("com.mojang:minecraft:${mcVersion}")

	// apply the Manifold processor, do not remove this unless you want to swap back to Essential preprocessor
	implementation(annotationProcessor("systems.manifold:manifold-preprocessor:${manifold.manifoldVersion.get()}")!!)

	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		officialMojangMappings()
	})

	if (isFabric) {
		modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")

		if (mcVersion == "1.19.2")
			modApi("net.minecraftforge:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
		else
			modApi("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${property("deps.forgeconfigapi")}")
	}

	if (isForge) {
		"forge"("net.minecraftforge:forge:${mcVersion}-${property("deps.fml")}")
	}

	if (isNeo) {
		"neoForge"("net.neoforged:neoforge:${property("deps.fml")}")
		modApi("fuzs.forgeconfigapiport:forgeconfigapiport-neoforge:${property("deps.forgeconfigapi")}")
	}

	vineflowerDecompilerClasspath("org.vineflower:vineflower:1.10.1")
}

// Loom config
loom {
	accessWidenerPath.set(rootProject.file("src/main/resources/${mod.id}.accesswidener"))

	if (loader == "forge") forge {
		convertAccessWideners.set(true)
		mixinConfigs(
			"mixins.${mod.id}.json",
			//"${mod.id}-common.mixins.json",
			//"${mod.id}-compat.mixins.json"
		)
	} else if (loader == "neoforge") neoForge {

	}

	runConfigs["client"].apply {
		ideConfigGenerated(true)
		vmArgs("-Dmixin.debug.export=true")
		programArgs("--username=nthxny") // Mom look I'm in the codebase!
		runDir = "../../run/${stonecutter.current.project}/"
	}

	decompilers {
		get("vineflower").apply {
			options.put("mark-corresponding-synthetics", "1")
		}
	}
}

// Tasks
tasks.withType<JavaCompile>() {
	options.compilerArgs.add("-Xplugin:Manifold")
	// modify the JavaCompile task and inject our auto-generated Manifold symbols
	if(!this.name.startsWith("_")) { // check the name, so we don't inject into Forge internal compilation
		setupManifoldPreprocessors(options.compilerArgs, loader, projectDir, mcVersion, false)
	}
}

project.tasks.register("setupManifoldPreprocessors") {
	setupManifoldPreprocessors(ArrayList(), loader, projectDir, mcVersion, true)
}

tasks.setupChiseledBuild {
	finalizedBy("setupManifoldPreprocessors")
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

// Resources
tasks.processResources {
	inputs.property("version", mod.version)
	inputs.property("mc", mod.mcDep)

//	inputs.property("id", mod.id)
//	inputs.property("name", mod.name)
//	inputs.property("group", mod.group)
//	inputs.property("author", mod.author)
//	inputs.property("namespace", mod.namespace)
//	inputs.property("display_name", mod.displayName)

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
		"fml" to if (loader == "neoforge") "1" else "45",
		"mnd" to if (loader == "neoforge") "" else "mandatory = true"
	)

	filesMatching("fabric.mod.json") { expand(map) }
	filesMatching("META-INF/mods.toml") { expand(map) }
	filesMatching("META-INF/neoforge.mods.toml") { expand(map) }
}

//yamlang {
//	targetSourceSets.set(mutableListOf(sourceSets["main"]))
//	inputDir.set("assets/${mod.id}/lang")
//}

// Env configuration
stonecutter {
	val j21 = eval(mcVersion, ">=1.20.6")
	java {
		withSourcesJar()
		sourceCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
		targetCompatibility = if (j21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	}

	kotlin {
		jvmToolchain(if (j21) 21 else 17)
	}
}

// Publishing
publishMods {
	file = tasks.remapJar.get().archiveFile
	additionalFiles.from(tasks.remapSourcesJar.get().archiveFile)
	displayName =
		"${mod.name} ${loader.replaceFirstChar { it.uppercase() }} ${mod.version} for ${property("mod.mc_title")}"
	version = mod.version
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE
	modLoaders.add(loader)

	val targets = property("mod.mc_targets").toString().split(' ')

	dryRun = providers.environmentVariable("MODRINTH_TOKEN")
		.getOrNull() == null || providers.environmentVariable("CURSEFORGE_TOKEN").getOrNull() == null

	modrinth {
		projectId = property("publish.modrinth").toString()
		accessToken = providers.environmentVariable("MODRINTH_TOKEN")
		targets.forEach(minecraftVersions::add)
		if (isFabric) {
			requires("fabric-api", "fabric-language-kotlin")
			optional("modmenu")
		} else requires("kotlin-for-forge")
		optional("yacl")
	}

	curseforge {
		projectId = property("publish.curseforge").toString()
		accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
		targets.forEach(minecraftVersions::add)
		if (isFabric) {
			requires("fabric-api", "fabric-language-kotlin")
			optional("modmenu")
		} else requires("kotlin-for-forge")
		optional("yacl")
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			groupId = "${property("mod.group")}.${mod.id}"
			artifactId = mod.version
			version = mcVersion

			from(components["java"])
		}
	}
}

fun setupManifoldPreprocessors(compilerArgs: MutableList<String>?, loader: String, parent: File, mcString: String, clearMainProject: Boolean) {
	val mcVers = listOf("1.18.2", "1.19.2", "1.20.1", "1.21")
	val mcIndex = mcVers.indexOf(mcString)
	val argList = ArrayList<String>()

	for (i in mcVers.indices) {
		val mcStr = mcVers[i].replace(".", "_").substring(2)
		if (mcIndex < i) argList.add("BEFORE_$mcStr")
		if (mcIndex <= i) argList.add("UPTO_$mcStr")
		if (mcIndex == i) argList.add("CURRENT_$mcStr")
		if (mcIndex > i) argList.add("NEWER_THAN_$mcStr")
		if (mcIndex >= i) argList.add("AFTER_$mcStr")
	}

	when (loader) {
		"fabric" -> argList.add("FABRIC")
		"forge" -> {
			argList.add("FORGE")
			argList.add("FORGELIKE")
		}
		"neoforge" -> {
			argList.add("NEO")
			argList.add("FORGELIKE")
		}
	}

	val sb = StringBuilder().append("# DO NOT EDIT - GENERATED BY THE BUILD SCRIPT\n")
	for (arg in argList) {
		compilerArgs?.add("-A$arg")
		sb.append(arg).append("=\n")
	}

	File(parent, "build.properties").writeText(sb.toString())
	File(parent, "build/chiseledSrc").createDirectory()
	File(parent, "build/chiseledSrc/build.properties").writeText(sb.toString())

	if (stonecutter.active.project == stonecutter.current.project)
		File(parent, "../../src/main/build.properties").writeText(sb.toString())

	if (clearMainProject)
		File(parent, "../../src/main/build.properties").delete()
}

///
/// Remove this after renaming your mod!
///
tasks.register<RenameExampleMod>("renameExampleMod", rootDir, mod.id, mod.name, mod.displayName, mod.namespace, mod.author).configure {
	group = "build helpers"
	description = "Renames the example mod to match the mod ID, name, and display name in gradle.properties"
}

abstract class RenameExampleMod @Inject constructor(private val dir: File, private val modId: String, private val modName: String, private val modDisplayName: String, private val rootNS: String, private val authorID: String) : DefaultTask()
{
	@TaskAction
	fun update() {
		dir.walk()
			.filter { it.name.endsWith(".java") || it.name.endsWith(".json") }
			.forEach {
				val text = it.readText()
					.replace("example_mod", modId)
					.replace("ExampleMod", modName)
					.replace("Example Mod", modDisplayName)

				it.writeText(text)
			}

		val javaDir = File(dir, "src/main/java/")
		val resourcesDir = File(dir, "src/main/resources/")
		val modDir = File(javaDir, "toni/examplemod/")

		rename(resourcesDir, "mixins.example_mod.json", "mixins.$modId.json")
		rename(resourcesDir, "example_mod.accesswidener", "$modId.accesswidener")

		rename(resourcesDir, "assets/example_mod", "assets/$modId")
		rename(resourcesDir, "data/example_mod", "data/$modId")

		rename(modDir, "ExampleMod.java", "$modName.java")

		rename(javaDir, "toni/examplemod/", "toni/$rootNS/")
		rename(javaDir, "toni/", "$authorID/")
	}

	private fun rename(targetDir: File, from: String, to: String) {
		File(targetDir, from).renameTo(File(targetDir, to))
	}
}
///
/// ^ Remove this after renaming your mod! ^
///