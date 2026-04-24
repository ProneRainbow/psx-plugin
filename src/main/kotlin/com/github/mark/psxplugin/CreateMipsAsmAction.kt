package com.github.mark.psxplugin

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager

class CreateMipsAsmAction : CreateFileFromTemplateAction(
    "MIPS Assembly File",
    "Creates a new PSX MIPS Assembly file with build directives.",
    PsxIcons.PsxLogo
) {

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder
            .setTitle("New MIPS Assembly File")
            .addKind("MIPS Assembly File", PsxIcons.PsxLogo, "MIPS Assembly")
    }

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String): String =
        "Create MIPS Assembly File $newName"

    override fun createFile(name: String, templateName: String, dir: PsiDirectory): PsiFile? {
        val project = dir.project
        val fileName = if (name.endsWith(".asm")) name else "$name.asm"
        val baseName = if (name.endsWith(".asm")) name.substringBeforeLast(".") else name
        
        val content = """
            .psx
            .if defined(IS_BUILD_DIR)
                .create "build/${baseName.lowercase()}.bin", 0x80010000
            .else
                .create "${baseName.lowercase()}.bin", 0x80010000
            .endif

            .org 0x80010000

            main:
                li t0, 0x1234

            loop:
                j loop
                nop
        """.trimIndent()

        val factory = PsiFileFactory.getInstance(project)
        val file = factory.createFileFromText(fileName, MipsFileType, content)
        val addedFile = dir.add(file) as PsiFile
        
        // Open the newly created file
        FileEditorManager.getInstance(project).openFile(addedFile.virtualFile, true)
        
        return addedFile
    }
}
