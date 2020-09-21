plugins {
    id("org.jetbrains.intellij")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(project(":openapi-load"))
    implementation(project(":codegen-cli"))

	testImplementation(kotlin("test-junit"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

group = "pro.bilous.code"
version = "0.0.7"

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
//    pulginName = "DifHub"
    version = "2020.2.2"
}

tasks.patchPluginXml {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
    sinceBuild("192")
}
