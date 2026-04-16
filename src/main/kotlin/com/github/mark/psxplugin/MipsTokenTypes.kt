package com.github.mark.psxplugin

import com.intellij.psi.tree.IElementType

object MipsTokenTypes {
    val KEYWORD = IElementType("MIPS_KEYWORD", MipsLanguage)
    val DIRECTIVE = IElementType("MIPS_DIRECTIVE", MipsLanguage)
    val LABEL = IElementType("MIPS_LABEL", MipsLanguage)
    val REGISTER = IElementType("MIPS_REGISTER", MipsLanguage)
    val COMMENT = IElementType("MIPS_COMMENT", MipsLanguage)
    val STRING = IElementType("MIPS_STRING", MipsLanguage)
    val NUMBER = IElementType("MIPS_NUMBER", MipsLanguage)
    val IDENTIFIER = IElementType("MIPS_IDENTIFIER", MipsLanguage)
    val WHITE_SPACE = com.intellij.psi.TokenType.WHITE_SPACE
    val COLON = IElementType("MIPS_COLON", MipsLanguage)
}
