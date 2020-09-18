package bhn.intellij.plugin

import com.intellij.CommonBundle
import com.intellij.reference.SoftReference
import org.jetbrains.annotations.PropertyKey
import java.lang.ref.Reference
import java.util.*

object MdBundle {
    private var ourBundle: Reference<ResourceBundle?>? = null
    private const val BUNDLE = "messages.MdBundle"

    fun message(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): String? {
        return CommonBundle.message(getBundle(), key, *params)
    }

    private fun getBundle(): ResourceBundle {
        var bundle = SoftReference.dereference(ourBundle)
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE)
            ourBundle = java.lang.ref.SoftReference(bundle)
        }
        return bundle!!
    }
}