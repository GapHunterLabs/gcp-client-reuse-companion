package dev.gaphunter.gcpclientreusecompanion.detect

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import dev.gaphunter.gcpclientreusecompanion.model.ClientBuildHit
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstructor
import org.jetbrains.kotlin.psi.KtDotQualifiedExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid

/** Kotlin counterpart of [JavaClientBuildFinder]. */
object KotlinClientBuildFinder {

    fun findAll(file: PsiFile): List<ClientBuildHit> {
        if (file !is KtFile) return emptyList()
        val hits = mutableListOf<ClientBuildHit>()
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitDotQualifiedExpression(expression: KtDotQualifiedExpression) {
                super.visitDotQualifiedExpression(expression)
                hitFor(expression)?.let { hits += it }
            }
        })
        return hits
    }

    private fun hitFor(serviceExpr: KtDotQualifiedExpression): ClientBuildHit? {
        val serviceCall = serviceExpr.selectorExpression as? KtCallExpression ?: return null
        if (serviceCall.calleeExpression?.text != "getService") return null

        val defaultInstanceExpr = serviceExpr.receiverExpression as? KtDotQualifiedExpression ?: return null
        val defaultInstanceCall = defaultInstanceExpr.selectorExpression as? KtCallExpression ?: return null
        if (defaultInstanceCall.calleeExpression?.text != "getDefaultInstance") return null

        val optionsClassRef = defaultInstanceExpr.receiverExpression as? KtNameReferenceExpression ?: return null
        val optionsClassName = optionsClassRef.getReferencedName()
        if (optionsClassName !in GcpClientSignals.OPTIONS_CLASS_NAMES) return null

        // Not inside a constructor -- covers a class primary/secondary
        // constructor body, a legitimate "create once" location.
        if (PsiTreeUtil.getParentOfType(serviceExpr, KtConstructor::class.java) != null) return null
        // A top-level/object property initializer (`val client = ...`)
        // outside any function is also a legitimate "create once"
        // location -- only flag when inside a real function body.
        if (PsiTreeUtil.getParentOfType(serviceExpr, KtNamedFunction::class.java) == null) return null

        return ClientBuildHit(leafOf(serviceExpr), optionsClassName)
    }

    private fun leafOf(element: PsiElement): PsiElement {
        var current = element
        while (current.firstChild != null) current = current.firstChild
        return current
    }
}
