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

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "jacoco")

	jacoco {
		toolVersion = "0.8.5"
	}

	tasks.jacocoTestReport {
		reports {
			xml.isEnabled = true
		}
	}

	tasks.test {
		useJUnitPlatform()
		finalizedBy(tasks.jacocoTestReport)
	}
	tasks.jacocoTestReport {
		dependsOn(tasks.test)
	}
}
