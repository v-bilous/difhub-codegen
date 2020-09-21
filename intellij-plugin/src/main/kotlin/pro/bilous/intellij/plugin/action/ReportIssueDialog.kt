package pro.bilous.intellij.plugin.action

import pro.bilous.intellij.plugin.MdBundle
import com.intellij.diagnostic.DiagnosticBundle
import com.intellij.diagnostic.HeapDumpAnalysisSupport
import com.intellij.diagnostic.hprof.action.getHeapDumpReportText
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.application.ApplicationNamesInfo
import com.intellij.openapi.application.impl.ApplicationInfoImpl
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.SwingHelper
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.event.HyperlinkEvent

class ReportIssueDialog : DialogWrapper(true) {

    val textArea: JTextArea = JTextArea(20, 100)

    init {
        textArea.text = ""
        textArea.isEditable = true
        textArea.caretPosition = 0
        init()
        title = "Report MD2.0 Plugin Issue"
        isModal = true
    }

    override fun createCenterPanel(): JComponent? {
        val pane = JPanel(BorderLayout(0, 5))
        val productName = "MD2.0 Framework Intellij Plugin"
        val vendorName = "MD2.0 Platform Team"

        val header = JLabel(MdBundle.message("reportIssue.dialog.header", productName, vendorName))

        pane.add(header, BorderLayout.PAGE_START)
        pane.add(JBScrollPane(textArea), BorderLayout.CENTER)
        with(SwingHelper.createHtmlViewer(true, null, JBColor.WHITE, JBColor.BLACK)) {
            isOpaque = false
            isFocusable = false
            addHyperlinkListener {
                if (it.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                    it.url?.let(BrowserUtil::browse)
                }
            }
            text = MdBundle.message("reportIssue.dialog.footer",
                ApplicationInfo.getInstance().shortCompanyName, HeapDumpAnalysisSupport.getInstance().getPrivacyPolicyUrl())
            pane.add(this, BorderLayout.PAGE_END)
        }

        return pane
    }
}
