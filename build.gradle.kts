plugins {
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.intellij") version "0.4.12" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "0.7" apply false
	jacoco
}

group = "pro.bilous"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
		mavenLocal()
        mavenCentral()
        jcenter()
    }
    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<JacocoReport> {
	executionData(
		fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec")
	)

	reports {
		xml.isEnabled = true
		xml.destination = file("${buildDir}/reports/jacoco/report.xml")
		html.isEnabled = false
		csv.isEnabled = false
	}
}
