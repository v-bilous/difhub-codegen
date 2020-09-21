package pro.bilous.intellij.plugin.project

import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.openapi.options.ConfigurationException
import com.intellij.ui.TextFieldWithHistory
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import org.apache.commons.lang3.StringUtils
import pro.bilous.difhub.config.ConfigReader
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import javax.swing.JComponent

class ServerSelectionStep(val moduleBuilder: ProjectModuleBuilder) : ModuleWizardStep() {

    private val userField = JBTextField()
    private val passwordField = JBTextField()
    private val textFieldWithHistory = TextFieldWithHistory()

    val difhub = ConfigReader.loadConfig().difhub

    override fun updateDataModel() {
		moduleBuilder.request.username = userField.text
		moduleBuilder.request.password = passwordField.text
		moduleBuilder.request.organization = textFieldWithHistory.text
		writeCredentialsToProps()
    }

    override fun getComponent(): JComponent {
		loadCredentials()
        return panel {
            titledRow("DifHub Credentials") {
                row {
                    cell {
                        label("Username:")
                        userField()
                    }
                }.enabled = true
                row {
                    cell {
                        label("Password: ")
                        passwordField()
                    }
                }.enabled = true
            }
            titledRow("Configure DifHub organization") {
                row {
                    cell {
                        label("Organization Name")
                        textFieldWithHistory()
                    }
                }.enabled = true
            }
        }
    }

    override fun validate(): Boolean {
        if (userField.text.isEmpty()) {
            throw ConfigurationException("Username must be set")
        }
        if (passwordField.text.isEmpty()) {
            throw ConfigurationException("Password must be set")
        }

        val serverUrl = "${difhub.api}/${URLEncoder.encode(textFieldWithHistory.text, "utf-8")}"

        if (StringUtils.isEmpty(serverUrl)) {
            throw ConfigurationException("Organization Name must be set")
        }
        try {
            URI(serverUrl)
            return true
        } catch (e: URISyntaxException) {
            throw ConfigurationException("Server URL must be a valid url")
        }
    }

	private fun loadCredentials() {
		textFieldWithHistory.text = System.getProperty("DIFHUB_ORG_NAME", "")
		userField.text = System.setProperty("DIFHUB_USERNAME", "")
		passwordField.text = System.setProperty("DIFHUB_PASSWORD", "")
	}

    private fun writeCredentialsToProps() {
        System.setProperty("DIFHUB_ORG_NAME", textFieldWithHistory.text)
        System.setProperty("DIFHUB_USERNAME", userField.text)
        System.setProperty("DIFHUB_PASSWORD", passwordField.text)
    }

}
