package com.github.mark.psxplugin

import com.intellij.codeInsight.template.TemplateContextType
import com.intellij.psi.PsiFile

class MipsLiveTemplateContext : TemplateContextType("MIPS", "MIPS") {
    override fun isInContext(file: PsiFile, offset: Int): Boolean {
        return file.language is MipsLanguage
    }
}
