plugins {
    id("org.jetbrains.intellij")
    kotlin("jvm")
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
