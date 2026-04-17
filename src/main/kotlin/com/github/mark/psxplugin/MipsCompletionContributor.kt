package com.github.mark.psxplugin

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext

class MipsCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    resultSet: CompletionResultSet
                ) {
                    val prefix = resultSet.prefixMatcher.prefix
                    
                    if (prefix.startsWith("$")) {
                        registers.forEach {
                            resultSet.addElement(LookupElementBuilder.create("$" + it).withIcon(PsxIcons.PsxLogo))
                        }
                    } else {
                        instructions.forEach {
                            resultSet.addElement(LookupElementBuilder.create(it).withBoldness(true))
                        }
                        directives.forEach {
                            resultSet.addElement(LookupElementBuilder.create(it).withItemTextForeground(com.intellij.ui.JBColor.GRAY))
                        }
                    }
                }
            }
        )
    }

    companion object {
        private val instructions = listOf(
            "add", "addu", "addi", "addiu", "sub", "subu", "mult", "multu", "div", "divu",
            "and", "or", "xor", "nor", "andi", "ori", "xori", "lui",
            "sll", "srl", "sra", "sllv", "srlv", "srav",
            "slt", "sltu", "slti", "sltiu",
            "beq", "bne", "bgez", "bgezal", "bgtz", "blez", "bltz", "bltzal",
            "j", "jal", "jr", "jalr",
            "lb", "lbu", "lh", "lhu", "lw", "sb", "sh", "sw",
            "mfhi", "mthi", "mflo", "mtlo",
            "mfc0", "mtc0", "mfc2", "mtc2", "cfc2", "ctc2",
            "syscall", "break", "nop",
            // Pseudo-instructions
            "li", "la", "move", "b", "bal", "beqz", "bnez"
        )

        private val registers = listOf(
            "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
            "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
            "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
            "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra",
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"
        )

        private val directives = listOf(
            ".text", ".data", ".word", ".byte", ".ascii", ".asciiz", ".align", ".org",
            ".set", ".global", ".ent", ".end", ".psx", ".create", ".close", ".equ"
        )
    }
}
