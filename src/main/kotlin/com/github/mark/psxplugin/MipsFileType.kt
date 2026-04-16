package com.github.mark.psxplugin

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object MipsFileType : LanguageFileType(MipsLanguage) {
    override fun getName(): String = "MIPS Assembly"
    override fun getDescription(): String = "MIPS Assembly language file"
    override fun getDefaultExtension(): String = "asm"
    override fun getIcon(): Icon = IconLoader.getIcon("/icons/mips.png", MipsFileType::class.java)
}
