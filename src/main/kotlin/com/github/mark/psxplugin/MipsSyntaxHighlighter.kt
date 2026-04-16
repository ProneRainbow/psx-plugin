package com.github.mark.psxplugin

import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.tree.IElementType

class MipsSyntaxHighlighter : SyntaxHighlighterBase() {
    companion object {
        val KEYWORD = createTextAttributesKey("MIPS_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
        val DIRECTIVE = createTextAttributesKey("MIPS_DIRECTIVE", DefaultLanguageHighlighterColors.METADATA)
        val LABEL = createTextAttributesKey("MIPS_LABEL", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
        val REGISTER = createTextAttributesKey("MIPS_REGISTER", DefaultLanguageHighlighterColors.GLOBAL_VARIABLE)
        val COMMENT = createTextAttributesKey("MIPS_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT)
        val STRING = createTextAttributesKey("MIPS_STRING", DefaultLanguageHighlighterColors.STRING)
        val NUMBER = createTextAttributesKey("MIPS_NUMBER", DefaultLanguageHighlighterColors.NUMBER)
    }

    override fun getHighlightingLexer(): Lexer {
        return MipsLexerAdapter()
    }

    override fun getTokenHighlights(tokenType: IElementType): Array<TextAttributesKey> {
        return when (tokenType) {
            MipsTokenTypes.KEYWORD -> arrayOf(KEYWORD)
            MipsTokenTypes.DIRECTIVE -> arrayOf(DIRECTIVE)
            MipsTokenTypes.LABEL -> arrayOf(LABEL)
            MipsTokenTypes.REGISTER -> arrayOf(REGISTER)
            MipsTokenTypes.COMMENT -> arrayOf(COMMENT)
            MipsTokenTypes.STRING -> arrayOf(STRING)
            MipsTokenTypes.NUMBER -> arrayOf(NUMBER)
            else -> emptyArray()
        }
    }
}
