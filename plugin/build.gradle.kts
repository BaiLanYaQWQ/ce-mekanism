plugins {
    java
}

group = "net.kuzuanpa"
version = "0.1.0"

// 编译期依赖全部 compileOnly：运行时由 CraftEngine 插件与 Paper 提供。
// paper-api 用 1.21.11（Java 21 字节码），本机 JDK 21 即可编译；服务端是 26.3（class 69）。
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.momirealms.net/releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.momirealms:craft-engine-core:26.9.1")
    compileOnly("net.momirealms:craft-engine-bukkit:26.9.1")
    // 核心是 JDK 21 字节码、零外部依赖：打进插件 jar，Paper 的 plugins/ 不放依赖 jar
    implementation(files("../../craftMekanism/core/build/libs/craftmekanism-core-0.1.0.jar"))
}

// Rendering regression tests inspect Paper transformations and their JOML rotations.
configurations.named("testCompileOnly") { extendsFrom(configurations.compileOnly.get()) }

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.jar {
    from(
        configurations.runtimeClasspath.get()
            .filter { it.name.startsWith("craftmekanism-core") }
            .map { zipTree(it) }
    ) {
        exclude("META-INF/MANIFEST.MF")
    }
}

tasks.processResources {
    val version = project.version.toString()
    inputs.property("version", version)
    filesMatching("plugin.yml") {
        expand("version" to version)
    }
}

// Pure storage invariants run without a Paper server; platform and persistence are also checked headlessly.
tasks.register<JavaExec>("storageSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    // Filter codecs reference Bukkit registry types even when the tested path stores selectors only.
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.storage.StorageSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("storageSelfTest")) }

tasks.register<JavaExec>("guiSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    // Runtime layout parsing uses Bukkit's YAML API, which is compileOnly in the shipped plugin.
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.gui.GuiLayoutSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("guiSelfTest")) }

tasks.register<JavaExec>("recipeDisplaySelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("net.kuzuanpa.cemekanism.machine.RecipeDisplaySelfTest")
}
tasks.named("check") { dependsOn(tasks.named("recipeDisplaySelfTest")) }

tasks.register<JavaExec>("recipeBrowserSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    // Generated machine registrations reference platform event types even without starting Paper.
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.machine.RecipeBrowserSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("recipeBrowserSelfTest")) }

// Verification runs the executable self-test suites registered in check.
tasks.named<Test>("test") { enabled = false }

tasks.register<JavaExec>("heaterSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.machine.HeaterSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("heaterSelfTest")) }

tasks.register<JavaExec>("appearanceSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.machine.AppearanceSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("appearanceSelfTest")) }

// Actual generated plant recipes and multiblock logic regression scenarios.
tasks.register<JavaExec>("plantSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.multiblock.PlantSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("plantSelfTest")) }

// Geometry shared by the actual multiblock display renderer.
tasks.register<JavaExec>("multiblockEffectSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.multiblock.MultiblockEffectSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("multiblockEffectSelfTest")) }

tasks.register<JavaExec>("multiblockDiagnosticSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.multiblock.MultiblockDiagnosticSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("multiblockDiagnosticSelfTest")) }

// Fuel accounting, real electrolysis recipes and config migration.
tasks.register<JavaExec>("energyBalanceSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.machine.EnergyBalanceSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("energyBalanceSelfTest")) }

tasks.register<JavaExec>("teleporterSelfTest") {
    group = "verification"
    dependsOn(tasks.named("testClasses"))
    classpath = sourceSets["test"].runtimeClasspath + configurations.compileClasspath.get()
    mainClass.set("net.kuzuanpa.cemekanism.multiblock.TeleporterSelfTest")
}
tasks.named("check") { dependsOn(tasks.named("teleporterSelfTest")) }
