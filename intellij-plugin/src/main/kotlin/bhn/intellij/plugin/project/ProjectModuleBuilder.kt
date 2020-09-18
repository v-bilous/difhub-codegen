package bhn.intellij.plugin.project

import bhn.intellij.plugin.Icons
import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider

class ProjectModuleBuilder: ModuleBuilder() {

    var request = ProjectCreationRequest()
    val projectFilesCreator = ProjectFilesCreator()

    override fun getModuleType() = StdModuleTypes.JAVA
    override fun getNodeIcon() = Icons.SpringBoot
    override fun getBuilderId() = "Create new System"
    override fun getDescription() = "Bootstrap system using DifHub"
    override fun getPresentableName() = "Bootstrap from DifHub"
    override fun getParentGroup() = "Build Tools"

    override fun createWizardSteps(wizardContext: WizardContext,
                                   modulesProvider: ModulesProvider): Array<ModuleWizardStep> {
        return arrayOf(ProjectDetailsStep(this, wizardContext))
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        doAddContentEntry(modifiableRootModel)
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return ServerSelectionStep(this)
    }

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? {
        val moduleSettings = settingsStep.moduleNameLocationSettings
        if (moduleSettings != null) {
            moduleSettings.moduleName = request.artifactId
        }
        return super.modifySettingsStep(settingsStep)
    }

    override fun createModule(moduleModel: ModifiableModuleModel): Module {
        val module = super.createModule(moduleModel)

        projectFilesCreator.createFiles(module, request)

        return module
    }
}