package dev.gaphunter.gcpclientreusecompanion.gutter

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import dev.gaphunter.gcpclientreusecompanion.detect.JavaClientBuildFinder
import dev.gaphunter.gcpclientreusecompanion.detect.KotlinClientBuildFinder
import dev.gaphunter.gcpclientreusecompanion.model.ClientBuildHit
import dev.gaphunter.gcpclientreusecompanion.review.ReviewPrompt

class ClientBuiltPerCallLineMarkerProvider : LineMarkerProviderDescriptor(), DumbAware {

    override fun getName(): String = "Google Cloud client built inside a method"

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? = null

    override fun collectSlowLineMarkers(elements: MutableList<out PsiElement>, result: MutableCollection<in LineMarkerInfo<*>>) {
        val file = elements.firstOrNull()?.containingFile ?: return
        val hits = when (file.language.id) {
            "JAVA" -> JavaClientBuildFinder.findAll(file)
            "kotlin" -> KotlinClientBuildFinder.findAll(file)
            else -> emptyList()
        }
        if (hits.isEmpty()) return

        val hitsByElement = hits.associateBy { it.callElement }
        for (element in elements) {
            val hit = hitsByElement[element] ?: continue
            result.add(buildMarker(hit))

            val path = file.virtualFile?.path ?: continue
            val lineNumber = file.viewProvider.document?.getLineNumber(element.textRange.startOffset) ?: -1
            ReviewPrompt.recordHit(file.project, "$path:$lineNumber")
        }
    }

    private fun buildMarker(hit: ClientBuildHit): LineMarkerInfo<PsiElement> {
        val tooltip = "${hit.optionsClassName}.getDefaultInstance().getService() is called here inside a method -- " +
            "Google Cloud's own client libraries best-practices guide says to reuse the same client object for many " +
            "requests instead of creating a new one each time; each new instance re-authenticates and has its own " +
            "credential cache, which can incur rate limiting if created too often"
        return LineMarkerInfo(
            hit.callElement,
            hit.callElement.textRange,
            ClientReuseIcons.RISK,
            { _: PsiElement -> tooltip },
            null,
            GutterIconRenderer.Alignment.RIGHT,
            { tooltip },
        )
    }
}
