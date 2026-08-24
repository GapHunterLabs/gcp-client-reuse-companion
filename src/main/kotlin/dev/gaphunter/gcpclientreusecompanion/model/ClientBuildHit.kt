package dev.gaphunter.gcpclientreusecompanion.model

import com.intellij.psi.PsiElement

/** One Google Cloud client `XxxOptions.getDefaultInstance().getService()` call found inside a non-constructor method body. */
data class ClientBuildHit(val callElement: PsiElement, val optionsClassName: String)
