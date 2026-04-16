package com.github.mark.psxplugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.openapi.diagnostic.Logger

class MipsDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        
        val text = target.text.trim().lowercase()
        
        // Handle Numbers
        tryConvertNumber(text)?.let { return it }

        // Handle Registers
        if (text.startsWith("$")) {
            val rawRegName = text.removePrefix("$")
            val regName = numericToNamed[rawRegName] ?: rawRegName
            return registerDocs[regName] ?: cop0Docs[regName] ?: gteDocs[regName]
        }
        
        // Handle Instructions
        return instructionDocs[text]
    }

    override fun getCustomDocumentationElement(editor: com.intellij.openapi.editor.Editor, file: PsiFile, contextElement: PsiElement?): PsiElement? {
        return contextElement
    }

    private fun tryConvertNumber(text: String): String? {
        val cleanText = text.lowercase().removeSuffix("h").removeSuffix("b")
        val longValue: Long = when {
            cleanText.startsWith("0x") -> cleanText.removePrefix("0x").toLongOrNull(16)
            cleanText.startsWith("0b") -> cleanText.removePrefix("0b").toLongOrNull(2)
            cleanText.all { it.isDigit() || it == '-' } -> cleanText.toLongOrNull()
            else -> null
        } ?: return null

        val dec = longValue.toString()
        val hex = "0x" + longValue.toString(16).uppercase()
        val bin = "0b" + longValue.toString(2)
        
        return """
            <b>Numeric Value</b><br>
            <hr>
            <table>
                <tr><td><b>Decimal:</b></td><td>$dec</td></tr>
                <tr><td><b>Hex:</b></td><td>$hex</td></tr>
                <tr><td><b>Binary:</b></td><td>$bin</td></tr>
            </table>
        """.trimIndent()
    }

    override fun getQuickNavigateInfo(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        val text = target.text.trim().lowercase()
        if (text.startsWith("$")) {
            val rawRegName = text.removePrefix("$")
            val namedReg = numericToNamed[rawRegName]
            val regLabel = if (namedReg != null) "\$$rawRegName (\$$namedReg)" else "\$$rawRegName"
            return "MIPS Register: $regLabel"
        }
        if (instructionDocs.containsKey(text)) {
            return "MIPS Instruction: $text"
        }
        return null
    }

    companion object {
        private val numericToNamed = mapOf(
            "0" to "zero", "1" to "at", "2" to "v0", "3" to "v1", "4" to "a0", "5" to "a1", "6" to "a2", "7" to "a3",
            "8" to "t0", "9" to "t1", "10" to "t2", "11" to "t3", "12" to "t4", "13" to "t5", "14" to "t6", "15" to "t7",
            "16" to "s0", "17" to "s1", "18" to "s2", "19" to "s3", "20" to "s4", "21" to "s5", "22" to "s6", "23" to "s7",
            "24" to "t8", "25" to "t9", "26" to "k0", "27" to "k1", "28" to "gp", "29" to "sp", "30" to "fp", "31" to "ra"
        )
        private val instructionDocs = mapOf(
            "add" to "<b>add rd, rs, rt</b>: Add (with overflow). rd = rs + rt. Exceptions: Overflow.",
            "addu" to "<b>addu rd, rs, rt</b>: Add Unsigned (no overflow). rd = rs + rt.",
            "addi" to "<b>addi rt, rs, immediate</b>: Add Immediate (with overflow). rt = rs + imm. Exceptions: Overflow.",
            "addiu" to "<b>addiu rt, rs, immediate</b>: Add Immediate Unsigned (no overflow). rt = rs + imm.",
            "sub" to "<b>sub rd, rs, rt</b>: Subtract (with overflow). rd = rs - rt. Exceptions: Overflow.",
            "subu" to "<b>subu rd, rs, rt</b>: Subtract Unsigned (no overflow). rd = rs - rt.",
            "mult" to "<b>mult rs, rt</b>: Multiply. (Hi, Lo) = rs * rt.",
            "multu" to "<b>multu rs, rt</b>: Multiply Unsigned. (Hi, Lo) = rs * rt.",
            "div" to "<b>div rs, rt</b>: Divide. Lo = rs / rt; Hi = rs % rt.",
            "divu" to "<b>divu rs, rt</b>: Divide Unsigned. Lo = rs / rt; Hi = rs % rt.",
            "and" to "<b>and rd, rs, rt</b>: Bitwise And. rd = rs & rt.",
            "or" to "<b>or rd, rs, rt</b>: Bitwise Or. rd = rs | rt.",
            "xor" to "<b>xor rd, rs, rt</b>: Bitwise Xor. rd = rs ^ rt.",
            "nor" to "<b>nor rd, rs, rt</b>: Bitwise Nor. rd = ~(rs | rt).",
            "andi" to "<b>andi rt, rs, immediate</b>: Bitwise And Immediate. rt = rs & zero_ext(imm).",
            "ori" to "<b>ori rt, rs, immediate</b>: Bitwise Or Immediate. rt = rs | zero_ext(imm).",
            "xori" to "<b>xori rt, rs, immediate</b>: Bitwise Xor Immediate. rt = rs ^ zero_ext(imm).",
            "lui" to "<b>lui rt, immediate</b>: Load Upper Immediate. rt = imm << 16.",
            "sll" to "<b>sll rd, rt, sa</b>: Shift Left Logical. rd = rt << sa.",
            "srl" to "<b>srl rd, rt, sa</b>: Shift Right Logical. rd = rt >> sa.",
            "sra" to "<b>sra rd, rt, sa</b>: Shift Right Arithmetic. rd = rt >> sa (sign-extended).",
            "sllv" to "<b>sllv rd, rt, rs</b>: Shift Left Logical Variable. rd = rt << rs.",
            "srlv" to "<b>srlv rd, rt, rs</b>: Shift Right Logical Variable. rd = rt >> rs.",
            "srav" to "<b>srav rd, rt, rs</b>: Shift Right Arithmetic Variable. rd = rt >> rs.",
            "slt" to "<b>slt rd, rs, rt</b>: Set on Less Than. rd = (rs < rt) ? 1 : 0.",
            "sltu" to "<b>sltu rd, rs, rt</b>: Set on Less Than Unsigned. rd = (rs < rt) ? 1 : 0.",
            "slti" to "<b>slti rt, rs, immediate</b>: Set on Less Than Immediate. rt = (rs < imm) ? 1 : 0.",
            "sltiu" to "<b>sltiu rt, rs, immediate</b>: Set on Less Than Immediate Unsigned. rt = (rs < imm) ? 1 : 0.",
            "beq" to "<b>beq rs, rt, label</b>: Branch on Equal. if (rs == rt) goto label.",
            "bne" to "<b>bne rs, rt, label</b>: Branch on Not Equal. if (rs != rt) goto label.",
            "bgez" to "<b>bgez rs, label</b>: Branch on Greater Than or Equal to Zero. if (rs >= 0) goto label.",
            "bgezal" to "<b>bgezal rs, label</b>: Branch on Greater Than or Equal to Zero and Link. if (rs >= 0) { ra = next_pc; goto label; }",
            "bgtz" to "<b>bgtz rs, label</b>: Branch on Greater Than Zero. if (rs > 0) goto label.",
            "blez" to "<b>blez rs, label</b>: Branch on Less Than or Equal to Zero. if (rs <= 0) goto label.",
            "bltz" to "<b>bltz rs, label</b>: Branch on Less Than Zero. if (rs < 0) goto label.",
            "bltzal" to "<b>bltzal rs, label</b>: Branch on Less Than Zero and Link. if (rs < 0) { ra = next_pc; goto label; }",
            "j" to "<b>j label</b>: Jump. goto label.",
            "jal" to "<b>jal label</b>: Jump and Link. ra = next_pc; goto label.",
            "jr" to "<b>jr rs</b>: Jump Register. goto rs.",
            "jalr" to "<b>jalr rd, rs</b>: Jump and Link Register. rd = next_pc; goto rs.",
            "lw" to "<b>lw rt, offset(base)</b>: Load Word. rt = memory[base + offset].",
            "lh" to "<b>lh rt, offset(base)</b>: Load Halfword. rt = memory[base + offset] (sign-extended).",
            "lhu" to "<b>lhu rt, offset(base)</b>: Load Halfword Unsigned. rt = memory[base + offset] (zero-extended).",
            "lb" to "<b>lb rt, offset(base)</b>: Load Byte. rt = memory[base + offset] (sign-extended).",
            "lbu" to "<b>lbu rt, offset(base)</b>: Load Byte Unsigned. rt = memory[base + offset] (zero-extended).",
            "sw" to "<b>sw rt, offset(base)</b>: Store Word. memory[base + offset] = rt.",
            "sh" to "<b>sh rt, offset(base)</b>: Store Halfword. memory[base + offset] = rt.",
            "sb" to "<b>sb rt, offset(base)</b>: Store Byte. memory[base + offset] = rt.",
            "mfhi" to "<b>mfhi rd</b>: Move From Hi. rd = Hi.",
            "mflo" to "<b>mflo rd</b>: Move From Lo. rd = Lo.",
            "mthi" to "<b>mthi rs</b>: Move To Hi. Hi = rs.",
            "mtlo" to "<b>mtlo rs</b>: Move To Lo. Lo = rs.",
            "syscall" to "<b>syscall</b>: System Call. Causes a System Call exception.",
            "break" to "<b>break</b>: Breakpoint. Causes a Breakpoint exception.",
            "nop" to "<b>nop</b>: No Operation. Does nothing.",
            "mfc0" to "<b>mfc0 rt, rd</b>: Move From Coprocessor 0. rt = COP0[rd].",
            "mtc0" to "<b>mtc0 rt, rd</b>: Move To Coprocessor 0. COP0[rd] = rt.",
            "mfc2" to "<b>mfc2 rt, rd</b>: Move From Coprocessor 2 (GTE). rt = GTE_Data[rd].",
            "mtc2" to "<b>mtc2 rt, rd</b>: Move To Coprocessor 2 (GTE). GTE_Data[rd] = rt.",
            "cfc2" to "<b>cfc2 rt, rd</b>: Copy From Control Coprocessor 2 (GTE). rt = GTE_Control[rd].",
            "ctc2" to "<b>ctc2 rt, rd</b>: Copy To Control Coprocessor 2 (GTE). GTE_Control[rd] = rt.",
            "li" to "<b>li rt, immediate</b>: Load Immediate (Pseudo-instruction). Loads a 32-bit constant into rt.",
            "la" to "<b>la rt, label</b>: Load Address (Pseudo-instruction). Loads the address of label into rt.",
            "move" to "<b>move rd, rs</b>: Move (Pseudo-instruction). rd = rs.",
            "b" to "<b>b label</b>: Branch (Pseudo-instruction). Unconditional branch to label.",
            "bal" to "<b>bal label</b>: Branch and Link (Pseudo-instruction). Unconditional branch to label and link ra.",
            "beqz" to "<b>beqz rs, label</b>: Branch if Equal to Zero (Pseudo-instruction). if (rs == 0) goto label.",
            "bnez" to "<b>bnez rs, label</b>: Branch if Not Equal to Zero (Pseudo-instruction). if (rs != 0) goto label."
        )
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
            "errorepc" to "<b>ErrorEPC</b> (COP0 R30): Error Exception Program Counter.",
            "badpaddr" to "<b>BadPAddr</b> (COP0 R31): Physical address of most recent virtual-memory error."
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
            "res1" to "<b>RES1</b> (GTE R23): Reserved register.",
            "mac0" to "<b>MAC0</b> (GTE R24): Accumulator 0 (S32).",
            "mac1" to "<b>MAC1</b> (GTE R25): Accumulator 1 (S32).",
            "mac2" to "<b>MAC2</b> (GTE R26): Accumulator 2 (S32).",
            "mac3" to "<b>MAC3</b> (GTE R27): Accumulator 3 (S32).",
            "irgb" to "<b>IRGB</b> (GTE R28): Input Color (U5).",
            "orgb" to "<b>ORGB</b> (GTE R29): Output Color (U5).",
            "lzcs" to "<b>LZCS</b> (GTE R30): Count Leading Zeroes/Ones Source.",
            "lzcr" to "<b>LZCR</b> (GTE R31): Count Leading Zeroes/Ones Result.",
            // GTE Control Registers
            "r11r12" to "<b>R11R12</b> (GTE C0): Rotation matrix (rows 1 & 2).",
            "r13r21" to "<b>R13R21</b> (GTE C1): Rotation matrix (rows 1 & 2).",
            "r22r23" to "<b>R22R23</b> (GTE C2): Rotation matrix (rows 2 & 3).",
            "r31r32" to "<b>R31R32</b> (GTE C3): Rotation matrix (row 3).",
            "r33" to "<b>R33</b> (GTE C4): Rotation matrix (row 3).",
            "trx" to "<b>TRX</b> (GTE C5): Translation vector X (S32).",
            "try" to "<b>TRY</b> (GTE C6): Translation vector Y (S32).",
            "trz" to "<b>TRZ</b> (GTE C7): Translation vector Z (S32).",
            "l11l12" to "<b>L11L12</b> (GTE C8): Light matrix (rows 1 & 2).",
            "l13l21" to "<b>L13L21</b> (GTE C9): Light matrix (rows 1 & 2).",
            "l22l23" to "<b>L22L23</b> (GTE C10): Light matrix (rows 2 & 3).",
            "l31l32" to "<b>L31L32</b> (GTE C11): Light matrix (row 3).",
            "l33" to "<b>L33</b> (GTE C12): Light matrix (row 3).",
            "rbk" to "<b>RBK</b> (GTE C13): Background color Red (S32).",
            "gbk" to "<b>GBK</b> (GTE C14): Background color Green (S32).",
            "bbk" to "<b>BBK</b> (GTE C15): Background color Blue (S32).",
            "lr1lr2" to "<b>LR1LR2</b> (GTE C16): Light source color matrix (rows 1 & 2).",
            "lr3lg1" to "<b>LR3LG1</b> (GTE C17): Light source color matrix (rows 1 & 2).",
            "lg2lg3" to "<b>LG2LG3</b> (GTE C18): Light source color matrix (rows 2 & 3).",
            "lb1lb2" to "<b>LB1LB2</b> (GTE C19): Light source color matrix (row 3).",
            "lb3" to "<b>LB3</b> (GTE C20): Light source color matrix (row 3).",
            "rfc" to "<b>RFC</b> (GTE C21): Far color Red (S32).",
            "gfc" to "<b>GFC</b> (GTE C22): Far color Green (S32).",
            "bfc" to "<b>BFC</b> (GTE C23): Far color Blue (S32).",
            "ofx" to "<b>OFX</b> (GTE C24): Screen offset X (S32).",
            "ofy" to "<b>OFY</b> (GTE C25): Screen offset Y (S32).",
            "h" to "<b>H</b> (GTE C26): Screen projection distance.",
            "dqa" to "<b>DQA</b> (GTE C27): Depth cueing parameter A (S16).",
            "dqb" to "<b>DQB</b> (GTE C28): Depth cueing parameter B (S32).",
            "zsf3" to "<b>ZSF3</b> (GTE C29): Z-scale factor 3 (S16).",
            "zsf4" to "<b>ZSF4</b> (GTE C30): Z-scale factor 4 (S16).",
            "flag" to "<b>FLAG</b> (GTE C31): Status flags for GTE operations."
        )
    }
}
