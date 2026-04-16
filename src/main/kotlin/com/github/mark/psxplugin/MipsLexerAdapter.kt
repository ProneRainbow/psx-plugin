package com.github.mark.psxplugin

import com.intellij.lexer.LexerBase
import com.intellij.psi.tree.IElementType

class MipsLexerAdapter : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var currentOffset: Int = 0
    private var tokenType: IElementType? = null
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0

    private val keywords = setOf(
        // Arithmetic
        "add", "addu", "sub", "subu", "addi", "addiu", "mult", "multu", "div", "divu",
        // Logical
        "and", "or", "xor", "nor", "andi", "ori", "xori", "lui",
        // Shift
        "sll", "srl", "sra", "sllv", "srlv", "srav",
        // Comparison
        "slt", "sltu", "slti", "sltiu",
        // Branch/Jump
        "beq", "bne", "bgtz", "blez", "j", "jal", "jr", "jalr",
        "bgez", "bgezal", "bltz", "bltzal",
        // Load/Store
        "lw", "lh", "lhu", "lb", "lbu", "sw", "sh", "sb", "lwl", "lwr", "swl", "swr",
        // System/Data movement
        "mfhi", "mflo", "mthi", "mtlo", "syscall", "break", "nop",
        // COP0 (System Control)
        "mfc0", "mtc0", "rfe",
        // COP2 (GTE - Geometry Transformation Engine)
        "mfc2", "mtc2", "cfc2", "ctc2",
        "rtps", "nclip", "op", "dpcs", "intpl", "sqr", "rtpt", "nct", "dcpl", "dpct",
        "ncc", "gpf", "gpl", "mvmva",
        // Pseudo-instructions (Common in MIPS)
        "li", "la", "move", "b", "bal", "beqz", "bnez"
    )

    private val registers = setOf(
        "zero", "at", "v0", "v1", "a0", "a1", "a2", "a3",
        "t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7",
        "s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7",
        "t8", "t9", "k0", "k1", "gp", "sp", "fp", "ra",
        // COP0 Registers
        "Index", "Random", "EntryLo0", "EntryLo1", "Context", "PageMask", "Wired", "BadVAddr",
        "Count", "EntryHi", "Compare", "Status", "Cause", "EPC", "PRId", "Config",
        "LLAddr", "WatchLo", "WatchHi", "XContext", "TagLo", "TagHi", "ErrorEPC",
        // GTE (COP2) Data Registers
        "VXY0", "VZ0", "VXY1", "VZ1", "VXY2", "VZ2", "RGB", "OTZ", "IR0", "IR1", "IR2", "IR3",
        "SXY0", "SXY1", "SXY2", "SXYP", "SZ0", "SZ1", "SZ2", "SZ3", "RGB0", "RGB1", "RGB2",
        "RES1", "MAC0", "MAC1", "MAC2", "MAC3", "IRGB", "ORGB", "LZCS", "LZCR",
        // GTE (COP2) Control Registers
        "R11R12", "R13R21", "R22R23", "R31R32", "R33", "TRX", "TRY", "TRZ",
        "L11L12", "L13L21", "L22L23", "L31L32", "L33", "RBK", "GBK", "BBK",
        "LR1LR2", "LR3LG1", "LG2LG3", "LB1LB2", "LB3", "RFC", "GFC", "BFC",
        "OFX", "OFY", "H", "DQA", "DQB", "ZSF3", "ZSF4", "FLAG"
    )

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.currentOffset = startOffset
        advance()
    }

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun advance() {
        if (currentOffset >= endOffset) {
            tokenType = null
            return
        }

        tokenStart = currentOffset
        val ch = buffer[currentOffset]

        when {
            ch.isWhitespace() -> {
                while (currentOffset < endOffset && buffer[currentOffset].isWhitespace()) {
                    currentOffset++
                }
                tokenType = MipsTokenTypes.WHITE_SPACE
            }
            ch == '#' || ch == ';' -> {
                while (currentOffset < endOffset && buffer[currentOffset] != '\n') {
                    currentOffset++
                }
                tokenType = MipsTokenTypes.COMMENT
            }
            ch == '"' -> {
                currentOffset++
                while (currentOffset < endOffset && buffer[currentOffset] != '"') {
                    if (buffer[currentOffset] == '\\' && currentOffset + 1 < endOffset) currentOffset++
                    currentOffset++
                }
                if (currentOffset < endOffset) currentOffset++
                tokenType = MipsTokenTypes.STRING
            }
            ch == '$' -> {
                currentOffset++
                val regStart = currentOffset
                while (currentOffset < endOffset && (buffer[currentOffset].isLetterOrDigit())) {
                    currentOffset++
                }
                // val regName = buffer.subSequence(regStart, currentOffset).toString()
                // Support both numeric ($0-$31) and named ($t0) registers
                tokenType = MipsTokenTypes.REGISTER
            }
            ch == ':' -> {
                currentOffset++
                tokenType = MipsTokenTypes.COLON
            }
            ch == '.' -> {
                currentOffset++
                while (currentOffset < endOffset && (buffer[currentOffset].isLetterOrDigit() || buffer[currentOffset] == '_')) {
                    currentOffset++
                }
                tokenType = MipsTokenTypes.DIRECTIVE
            }
            ch.isDigit() || (ch == '-' && currentOffset + 1 < endOffset && buffer[currentOffset + 1].isDigit()) -> {
                if (ch == '-') currentOffset++
                if (currentOffset + 1 < endOffset && buffer[currentOffset] == '0') {
                    val next = buffer[currentOffset + 1].lowercaseChar()
                    if (next == 'x') {
                        currentOffset += 2 // Hex
                        while (currentOffset < endOffset && (buffer[currentOffset].isDigit() || buffer[currentOffset].lowercaseChar() in 'a'..'f')) {
                            currentOffset++
                        }
                    } else if (next == 'b') {
                        currentOffset += 2 // Binary
                        while (currentOffset < endOffset && (buffer[currentOffset] == '0' || buffer[currentOffset] == '1')) {
                            currentOffset++
                        }
                    } else {
                        while (currentOffset < endOffset && buffer[currentOffset].isDigit()) {
                            currentOffset++
                        }
                    }
                } else {
                    while (currentOffset < endOffset && buffer[currentOffset].isDigit()) {
                        currentOffset++
                    }
                }
                tokenType = MipsTokenTypes.NUMBER
            }
            ch.isLetter() || ch == '_' -> {
                while (currentOffset < endOffset && (buffer[currentOffset].isLetterOrDigit() || buffer[currentOffset] == '_' || buffer[currentOffset] == '.')) {
                    currentOffset++
                }
                val text = buffer.subSequence(tokenStart, currentOffset).toString()
                
                when {
                    text in keywords -> tokenType = MipsTokenTypes.KEYWORD
                    text in registers -> tokenType = MipsTokenTypes.REGISTER
                    currentOffset < endOffset && buffer[currentOffset] == ':' -> {
                        tokenType = MipsTokenTypes.LABEL
                    }
                    else -> tokenType = MipsTokenTypes.IDENTIFIER
                }
            }
            else -> {
                currentOffset++
                tokenType = MipsTokenTypes.IDENTIFIER
            }
        }
        tokenEnd = currentOffset
    }

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset
}
