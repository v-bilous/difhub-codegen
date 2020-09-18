package bhn.intellij.plugin.project

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.layout.*
import java.awt.Dimension
import javax.swing.DefaultComboBoxModel
import javax.swing.JPanel

class ProjectDetails(moduleBuilder: ProjectModuleBuilder, wizardContext: WizardContext) {

    var rootPanel: DialogPanel? = null
    val request = moduleBuilder.request
    val difHubData = request.difHubData

    val systemComboBox = ComboBox(DefaultComboBoxModel(difHubData!!.keys.toTypedArray()))
//    val appComboBoxModel = DefaultComboBoxModel<String>()
//    val appComboBox = ComboBox<String>(appComboBoxModel)

    init {
        systemComboBox.addActionListener {
            request.system = systemComboBox.selectedItem as String
            selectSystemApplications(request.system)
        }
//        appComboBox.addActionListener {
//            if (appComboBox.selectedItem != null) {
//                request.application = appComboBox.selectedItem as String
//                selectApplication(request.application)
//                rootPanel?.reset()
//            }
//        }
        if (difHubData != null) {
            selectSystemApplications(difHubData.keys.first())
        }
    }

    fun fullPanel(): JPanel {
        rootPanel = panel(LCFlags.fillX) {
            titledRow("Select DifHub system") {
                row("Select System:") { systemComboBox() }
//                row("Select Application:") { appComboBox() }
            }
            titledRow("Configure project properties") {
                row("Group Id") { textField(request::groupId) }
                row("Artifact Id") { textField(request::artifactId) }
                row("Version") { textField(request::version) }
                row("Title") { textField(request::title) }
                row("Description") { textField(request::description) }
                row("Base Package") { textField(request::basePackage) }
//                row("DB Name") { textField(request::dbName) }
//                row("Binding Entity") { checkBox("", request::addBindingEntity) }
            }
        }
        rootPanel?.preferredSize = Dimension(400, 400)
        return rootPanel!!
    }

    private fun selectSystemApplications(value: String?) {
        if (value == null) {
            return
        }
//        appComboBoxModel.removeAllElements()
        val apps = difHubData?.get(value)
        request.applications.clear()
        request.applications.addAll(apps!!)
//        apps?.forEach {
//            appComboBoxModel.addElement(it)
//        }
//        appComboBox.selectedItem = apps?.first()
        request.system = value
        selectApplication(value)
        rootPanel?.reset()
    }

    private fun selectApplication(appName: String?) {
        requireNotNull(appName)
        val lower = appName.toLowerCase()
        request.groupId = "com.$lower"
        request.artifactId = lower
        request.title = "$appName System"
        request.description = "$appName System"
        request.basePackage = "com.${lower}"
//        request.dbName = "${lower}db"
    }
}