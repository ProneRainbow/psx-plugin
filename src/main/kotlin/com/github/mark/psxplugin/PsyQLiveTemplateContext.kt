package com.github.mark.psxplugin

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

class PsyQLiveTemplateContext : TemplateContextType("PSYQ", "PsyQ") {
    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        return file.name.endsWith(".c") || file.name.endsWith(".cpp") || file.name.endsWith(".h")
    }
}
