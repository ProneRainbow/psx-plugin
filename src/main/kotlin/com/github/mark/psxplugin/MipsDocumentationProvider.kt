package com.github.mark.psxplugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement

class MipsDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        if (target is LeafPsiElement && target.elementType == MipsTokenTypes.REGISTER) {
            val regName = target.text.removePrefix("$").lowercase()
            return registerDocs[regName] ?: cop0Docs[regName] ?: gteDocs[regName]
        }
        return null
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        if (target is LeafPsiElement && target.elementType == MipsTokenTypes.REGISTER) {
            val regName = target.text.removePrefix("$")
            return "MIPS Register: \$$regName"
        }
        return null
    }

    companion object {
        private val registerDocs = mapOf(
            "zero" to "<b>\$zero</b>: Always contains the value 0.",
            "at" to "<b>\$at</b>: Assembler Temporary. Reserved for use by the assembler.",
            "v0" to "<b>\$v0</b>: Expression evaluation and results of a function.",
            "v1" to "<b>\$v1</b>: Expression evaluation and results of a function.",
            "a0" to "<b>\$a0</b>: Argument 0. Used to pass the first parameter to a function.",
            "a1" to "<b>\$a1</b>: Argument 1. Used to pass the second parameter to a function.",
            "a2" to "<b>\$a2</b>: Argument 2. Used to pass the third parameter to a function.",
            "a3" to "<b>\$a3</b>: Argument 3. Used to pass the fourth parameter to a function.",
            "t0" to "<b>\$t0</b>: Temporary. Caller-saved. Subroutines can use without saving.",
            "t1" to "<b>\$t1</b>: Temporary. Caller-saved.",
            "t2" to "<b>\$t2</b>: Temporary. Caller-saved.",
            "t3" to "<b>\$t3</b>: Temporary. Caller-saved.",
            "t4" to "<b>\$t4</b>: Temporary. Caller-saved.",
            "t5" to "<b>\$t5</b>: Temporary. Caller-saved.",
            "t6" to "<b>\$t6</b>: Temporary. Caller-saved.",
            "t7" to "<b>\$t7</b>: Temporary. Caller-saved.",
            "s0" to "<b>\$s0</b>: Saved Temporary. Callee-saved. Must be preserved by subroutines.",
            "s1" to "<b>\$s1</b>: Saved Temporary. Callee-saved.",
            "s2" to "<b>\$s2</b>: Saved Temporary. Callee-saved.",
            "s3" to "<b>\$s3</b>: Saved Temporary. Callee-saved.",
            "s4" to "<b>\$s4</b>: Saved Temporary. Callee-saved.",
            "s5" to "<b>\$s5</b>: Saved Temporary. Callee-saved.",
            "s6" to "<b>\$s6</b>: Saved Temporary. Callee-saved.",
            "s7" to "<b>\$s7</b>: Saved Temporary. Callee-saved.",
            "t8" to "<b>\$t8</b>: Temporary. Caller-saved.",
            "t9" to "<b>\$t9</b>: Temporary. Caller-saved.",
            "k0" to "<b>\$k0</b>: Reserved for Kernel (Interrupt/Exception handling).",
            "k1" to "<b>\$k1</b>: Reserved for Kernel (Interrupt/Exception handling).",
            "gp" to "<b>\$gp</b>: Global Pointer. Points to the middle of the 64K block of static data.",
            "sp" to "<b>\$sp</b>: Stack Pointer. Points to the last location on the stack.",
            "fp" to "<b>\$fp</b>: Frame Pointer (or \$s8). Used to track the stack frame.",
            "ra" to "<b>\$ra</b>: Return Address. Stores the address to return to after a function call."
        )

        private val cop0Docs = mapOf(
            "index" to "<b>Index</b> (COP0 R0): Programmable index for TLB operations.",
            "random" to "<b>Random</b> (COP0 R1): Random index for TLB operations.",
            "entrylo0" to "<b>EntryLo0</b> (COP0 R2): Low-order 32 bits of TLB entry for even pages.",
            "entrylo1" to "<b>EntryLo1</b> (COP0 R3): Low-order 32 bits of TLB entry for odd pages.",
            "context" to "<b>Context</b> (COP0 R4): Pointer to kernel-page-table entry.",
            "pagemask" to "<b>PageMask</b> (COP0 R5): Page size mask for TLB entries.",
            "wired" to "<b>Wired</b> (COP0 R6): Number of wired TLB entries.",
            "badvaddr" to "<b>BadVAddr</b> (COP0 R8): Address of most recent virtual-memory error.",
            "count" to "<b>Count</b> (COP0 R9): Free-running timer counter.",
            "entryhi" to "<b>EntryHi</b> (COP0 R10): High-order 32 bits of TLB entry.",
            "compare" to "<b>Compare</b> (COP0 R11): Timer interrupt control.",
            "status" to "<b>Status</b> (COP0 R12): Processor status and control.",
            "cause" to "<b>Cause</b> (COP0 R13): Cause of most recent exception.",
            "epc" to "<b>EPC</b> (COP0 R14): Exception Program Counter. Address where processing resumes.",
            "prid" to "<b>PRId</b> (COP0 R15): Processor Revision Identifier.",
            "config" to "<b>Config</b> (COP0 R16): Configuration register for CPU.",
            "lladdr" to "<b>LLAddr</b> (COP0 R17): Load Linked Address.",
            "watchlo" to "<b>WatchLo</b> (COP0 R18): Watchpoint address (low).",
            "watchhi" to "<b>WatchHi</b> (COP0 R19): Watchpoint address (high).",
            "xcontext" to "<b>XContext</b> (COP0 R20): 64-bit context register.",
            "taglo" to "<b>TagLo</b> (COP0 R28): Cache tag (low).",
            "taghi" to "<b>TagHi</b> (COP0 R29): Cache tag (high).",
            "errorepc" to "<b>ErrorEPC</b> (COP0 R30): Error Exception Program Counter."
        )

        private val gteDocs = mapOf(
            "vxy0" to "<b>VXY0</b> (GTE R0): Vector 0 XY components (S16).",
            "vz0" to "<b>VZ0</b> (GTE R1): Vector 0 Z component (S16).",
            "vxy1" to "<b>VXY1</b> (GTE R2): Vector 1 XY components (S16).",
            "vz1" to "<b>VZ1</b> (GTE R3): Vector 1 Z component (S16).",
            "vxy2" to "<b>VXY2</b> (GTE R4): Vector 2 XY components (S16).",
            "vz2" to "<b>VZ2</b> (GTE R5): Vector 2 Z component (S16).",
            "rgb" to "<b>RGB</b> (GTE R6): Color/Code value (U8).",
            "otz" to "<b>OTZ</b> (GTE R7): Average Z value (U16).",
            "ir0" to "<b>IR0</b> (GTE R8): Intermediate Value 0 (S16).",
            "ir1" to "<b>IR1</b> (GTE R9): Intermediate Value 1 (S16).",
            "ir2" to "<b>IR2</b> (GTE R10): Intermediate Value 2 (S16).",
            "ir3" to "<b>IR3</b> (GTE R11): Intermediate Value 3 (S16).",
            "sxy0" to "<b>SXY0</b> (GTE R12): Screen XY 0 (S16).",
            "sxy1" to "<b>SXY1</b> (GTE R13): Screen XY 1 (S16).",
            "sxy2" to "<b>SXY2</b> (GTE R14): Screen XY 2 (S16).",
            "sxyp" to "<b>SXYP</b> (GTE R15): Screen XY FIFO (S16).",
            "sz0" to "<b>SZ0</b> (GTE R16): Screen Z 0 (U16).",
            "sz1" to "<b>SZ1</b> (GTE R17): Screen Z 1 (U16).",
            "sz2" to "<b>SZ2</b> (GTE R18): Screen Z 2 (U16).",
            "sz3" to "<b>SZ3</b> (GTE R19): Screen Z FIFO (U16).",
            "rgb0" to "<b>RGB0</b> (GTE R20): Color FIFO 0 (U8).",
            "rgb1" to "<b>RGB1</b> (GTE R21): Color FIFO 1 (U8).",
            "rgb2" to "<b>RGB2</b> (GTE R22): Color FIFO 2 (U8).",
            "mac0" to "<b>MAC0</b> (GTE R24): Accumulator 0 (S32).",
            "mac1" to "<b>MAC1</b> (GTE R25): Accumulator 1 (S32).",
            "mac2" to "<b>MAC2</b> (GTE R26): Accumulator 2 (S32).",
            "mac3" to "<b>MAC3</b> (GTE R27): Accumulator 3 (S32).",
            "irgb" to "<b>IRGB</b> (GTE R28): Input Color (U5).",
            "orgb" to "<b>ORGB</b> (GTE R29): Output Color (U5).",
            "lzcs" to "<b>LZCS</b> (GTE R30): Count Leading Zeroes/Ones Source.",
            "lzcr" to "<b>LZCR</b> (GTE R31): Count Leading Zeroes/Ones Result.",
            "flag" to "<b>FLAG</b> (GTE C31): Status flags for GTE operations."
        )
    }
}
