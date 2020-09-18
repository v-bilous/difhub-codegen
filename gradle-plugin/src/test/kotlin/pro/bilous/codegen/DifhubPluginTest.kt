package pro.bilous.codegen

import junit.framework.Assert.assertTrue
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert
import org.junit.Test

class DifhubPluginTest {
    @Test
    fun greeterPluginAddsGreetingTaskToProject() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("pro.bilous.codegen")


        assertTrue(project.pluginManager.hasPlugin("com.baeldung.greeting"));

        Assert.assertTrue(project.tasks.getByName("loadSpecs") is LoadSpecsTask)
    }
}