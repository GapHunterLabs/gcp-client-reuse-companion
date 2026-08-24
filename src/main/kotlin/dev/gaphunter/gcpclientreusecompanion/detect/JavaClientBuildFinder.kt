package dev.gaphunter.gcpclientreusecompanion.detect

import com.intellij.psi.JavaRecursiveElementWalkingVisitor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.gcpclientreusecompanion.model.ClientBuildHit

/**
 * Finds `XxxOptions.getDefaultInstance().getService()` calls (Google
 * Cloud client libraries, [GcpClientSignals]) written inside a
 * non-constructor method body -- Google Cloud's own client libraries
 * best-practices documentation states: "you should reuse the same
 * client object for many requests when possible, instead of creating
 * a new one for every request", explaining that each client instance
 * has its own credential cache and that "creating too many in a small
 * period of time may incur rate limiting causing library requests to
 * fail authentication".
 *
 * **v0.1 scope, stated honestly:** only flags the direct
 * `.getDefaultInstance().getService()` chain -- an options instance
 * assigned to an intermediate variable before `.getService()` is
 * called isn't specially traced. Doesn't flag a call inside a
 * constructor, a static initializer, or a field initializer (all
 * legitimate "create once" locations) -- matches by simple class
 * name, so it works whether the real Google Cloud jar is on the
 * classpath or not.
 */
object JavaClientBuildFinder {

    fun findAll(file: PsiFile): List<ClientBuildHit> {
        val hits = mutableListOf<ClientBuildHit>()
        file.accept(object : JavaRecursiveElementWalkingVisitor() {
            override fun visitMethodCallExpression(expression: PsiMethodCallExpression) {
                super.visitMethodCallExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(serviceCall: PsiMethodCallExpression): ClientBuildHit? {
        if (serviceCall.methodExpression.referenceName != "getService") return null

        val defaultInstanceCall = serviceCall.methodExpression.qualifierExpression as? PsiMethodCallExpression ?: return null
        if (defaultInstanceCall.methodExpression.referenceName != "getDefaultInstance") return null

        val optionsClassRef = defaultInstanceCall.methodExpression.qualifierExpression as? PsiReferenceExpression ?: return null
        val optionsClassName = optionsClassRef.referenceName ?: return null
        if (optionsClassName !in GcpClientSignals.OPTIONS_CLASS_NAMES) return null

        val containingMethod = PsiTreeUtil.getParentOfType(serviceCall, PsiMethod::class.java) ?: return null
        if (containingMethod.isConstructor) return null

        return ClientBuildHit(leafOf(serviceCall), optionsClassName)
    }

    /** Descends to a real leaf PSI element -- LineMarkerInfo must never anchor on a composite node (SDK_GOTCHAS.md SS20). */
    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
