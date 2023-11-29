import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}


val sqliteVersion: String by project
val junitVersion: String by project

dependencies {
    // that's the most important dependency. It gives us access to CfD for our laptop's OS
    implementation(compose.desktop.currentOs)

    implementation(compose.materialIconsExtended)

    // this gives us access to the UI-Thread
    //runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.6.1")

    // we work with a local sqlite-db in lots of examples
    implementation("org.xerial:sqlite-jdbc:$sqliteVersion")

    // for unit testing
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Metropolis"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

