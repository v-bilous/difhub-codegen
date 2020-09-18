package bhn.intellij.plugin.action.menu

import bhn.intellij.plugin.Icons
import bhn.intellij.plugin.gen.CodeGenerator
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class GenerateCodeAction : AnAction(Icons.project) {
    override fun actionPerformed(e: AnActionEvent) {
        val projectPath = e.project!!.basePath
        CodeGenerator().generate(projectPath!!)
    }
}