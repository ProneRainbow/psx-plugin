package com.github.mark.psxplugin

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.tree.IElementType
import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.PsiFileBase

class MipsParserDefinition : ParserDefinition {
    companion object {
        val FILE = IFileElementType(MipsLanguage)
    }

    override fun createLexer(project: Project?): Lexer = MipsLexerAdapter()

    override fun createParser(project: Project?): PsiParser = PsiParser { root, builder ->
        val rootMarker = builder.mark()
        while (!builder.eof()) {
            val tokenMarker = builder.mark()
            builder.advanceLexer()
            tokenMarker.done(builder.tokenType ?: MipsTokenTypes.IDENTIFIER)
        }
        rootMarker.done(root)
        builder.treeBuilt
    }

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = TokenSet.create(MipsTokenTypes.COMMENT)

    override fun getStringLiteralElements(): TokenSet = TokenSet.create(MipsTokenTypes.STRING)

    override fun createElement(node: ASTNode?): PsiElement = ASTWrapperPsiElement(node!!)

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return object : PsiFileBase(viewProvider, MipsLanguage) {
            override fun getFileType(): com.intellij.openapi.fileTypes.FileType = MipsFileType
        }
    }

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode?, right: ASTNode?): ParserDefinition.SpaceRequirements =
        ParserDefinition.SpaceRequirements.MAY
}
