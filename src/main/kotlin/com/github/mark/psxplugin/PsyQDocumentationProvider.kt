package com.github.mark.psxplugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.openapi.diagnostic.Logger

class PsyQDocumentationProvider : AbstractDocumentationProvider() {
    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        val text = target.text.trim()
        
        // 1. Handle Numbers
        tryConvertNumber(text)?.let { return it }

        // 2. Try exact match first
        val doc = functionDocs[text] ?: registerDocs[text] ?: constantDocs[text] ?: structDocs[text] ?: macroDocs[text]
        if (doc != null) return doc

        // 3. Handle MIPS registers in C files (e.g. "$t1" or just "t1" in some contexts)
        val regName = text.removePrefix("$").lowercase()
        return mipsRegisterDocs[regName]
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

    companion object {
        // ... existing maps ...
        private val mipsRegisterDocs = mapOf(
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
        private val macroDocs = mapOf(
            "setRECT" to "<b>setRECT(r, x, y, w, h)</b>: Helper macro to initialize a RECT structure.",
            "setVector" to "<b>setVector(v, x, y, z)</b>: Helper macro to initialize a VECTOR structure.",
            "setSVECTOR" to "<b>setSVECTOR(v, x, y, z)</b>: Helper macro to initialize an SVECTOR structure.",
            "setRGB0" to "<b>setRGB0(p, r, g, b)</b>: Helper macro to set the RGB color of a primitive.",
            "setaddr" to "<b>setaddr(p, a)</b>: Helper macro to set the pointer to the next primitive in an ordering table.",
            "setPolyF3" to "<b>setPolyF3(p)</b>: Helper macro to initialize a flat-shaded triangle primitive."
        )

        private val structDocs = mapOf(
            "RECT" to "<b>RECT</b>: Defines a rectangular area. Contains short x, y (top-left) and short w, h (width, height).",
            "SVECTOR" to "<b>SVECTOR</b>: Short vector. Contains short vx, vy, vz and short pad.",
            "VECTOR" to "<b>VECTOR</b>: Long vector. Contains long vx, vy, vz and long pad.",
            "DRAWENV" to "<b>DRAWENV</b>: Drawing environment structure. Defines clipping area, background color, etc.",
            "DISPENV" to "<b>DISPENV</b>: Display environment structure. Defines display area, interlace mode, etc.",
            "POLY_F3" to "<b>POLY_F3</b>: Flat-shaded triangle primitive structure.",
            "POLY_FT3" to "<b>POLY_FT3</b>: Flat-shaded textured triangle primitive structure.",
            "POLY_G3" to "<b>POLY_G3</b>: Gouraud-shaded triangle primitive structure.",
            "POLY_GT3" to "<b>POLY_GT3</b>: Gouraud-shaded textured triangle primitive structure.",
            "POLY_F4" to "<b>POLY_F4</b>: Flat-shaded quadrangle primitive structure.",
            "POLY_FT4" to "<b>POLY_FT4</b>: Flat-shaded textured quadrangle primitive structure.",
            "POLY_G4" to "<b>POLY_G4</b>: Gouraud-shaded quadrangle primitive structure.",
            "POLY_GT4" to "<b>POLY_GT4</b>: Gouraud-shaded textured quadrangle primitive structure.",
            "SPRT" to "<b>SPRT</b>: Free-size sprite primitive structure.",
            "TILE" to "<b>TILE</b>: Free-size tile primitive structure.",
            "GsOT" to "<b>GsOT</b>: Ordering Table structure for the libgs library.",
            "CdlFILE" to "<b>CdlFILE</b>: Structure containing file information for CD-ROM operations.",
            "TIM_IMAGE" to "<b>TIM_IMAGE</b>: Structure containing TIM image information (VRAM position, CLUT, etc.)."
        )

        private val constantDocs = mapOf(
            "GsNONINTER" to "<b>GsNONINTER</b>: Non-interlaced display mode. Provides a flicker-free image but at half the vertical resolution of interlaced.",
            "GsINTER" to "<b>GsINTER</b>: Interlaced display mode. Provides higher vertical resolution but may exhibit flicker on CRT screens.",
            "GsOFSGPU" to "<b>GsOFSGPU</b>: Use GPU offsets for screen centering.",
            "VMODE_NTSC" to "<b>VMODE_NTSC</b>: Video mode for North American and Japanese regions (60Hz).",
            "VMODE_PAL" to "<b>VMODE_PAL</b>: Video mode for European regions (50Hz).",
            "PAD_SELECT" to "<b>PAD_SELECT</b>: Bitmask for the Select button.",
            "PAD_START" to "<b>PAD_START</b>: Bitmask for the Start button.",
            "PAD_UP" to "<b>PAD_UP</b>: Bitmask for the D-Pad Up button.",
            "PAD_TRIANGLE" to "<b>PAD_TRIANGLE</b>: Bitmask for the Triangle button.",
            "PAD_CROSS" to "<b>PAD_CROSS</b>: Bitmask for the Cross (X) button.",
            "PAD_CIRCLE" to "<b>PAD_CIRCLE</b>: Bitmask for the Circle button.",
            "PAD_SQUARE" to "<b>PAD_SQUARE</b>: Bitmask for the Square button.",
            "CdlModeDouble" to "<b>CdlModeDouble</b>: Double speed reading mode (300KB/s).",
            "SPU_ON" to "<b>SPU_ON</b>: Constant to enable SPU voice or reverb."
        )

        private val registerDocs = mapOf(
            "I_STAT" to "<b>I_STAT</b> (0x1F801070): Interrupt status register. Read to see which interrupts are pending.",
            "I_MASK" to "<b>I_MASK</b> (0x1F801074): Interrupt mask register. Set bits to enable specific interrupts.",
            "GP0" to "<b>GP0</b> (0x1F801810): GPU Command/Data register. Used to send drawing commands and vertex data.",
            "GP1" to "<b>GP1</b> (0x1F801814): GPU Control/Status register. Used for display settings and checking GPU state.",
            "GPUSTAT" to "<b>GPUSTAT</b> (0x1F801814): GPU Status register (Read-only).",
            "T0_COUNT" to "<b>T0_COUNT</b> (0x1F801100): Timer 0 Current Count.",
            "T1_COUNT" to "<b>T1_COUNT</b> (0x1F801110): Timer 1 Current Count (Horizontal Retrace).",
            "T2_COUNT" to "<b>T2_COUNT</b> (0x1F801120): Timer 2 Current Count (1/8 of System Clock).",
            "DMA_MADR" to "<b>DMA_MADR</b>: Memory Address register for a DMA channel.",
            "DMA_CHCR" to "<b>DMA_CHCR</b>: Channel Control register for a DMA channel."
        )

        private val functionDocs = mapOf(
            "GsInitGraph" to "<b>GsInitGraph</b>: Initializes the graphics system with specified resolution and mode.",
            "GsSwapDisplay" to "<b>GsSwapDisplay</b>: Swaps the drawing and display buffers (Double buffering).",
            "GsSortObject4" to "<b>GsSortObject4</b>: Registers a 3D object for drawing in the ordering table.",
            "ResetGraph" to "<b>ResetGraph</b>: Resets the GPU and clears the VRAM.",
            "PutDrawEnv" to "<b>PutDrawEnv</b>: Sends a drawing environment structure to the GPU.",
            "PutDispEnv" to "<b>PutDispEnv</b>: Sends a display environment structure to the GPU.",
            "DrawOTag" to "<b>DrawOTag</b>: Initiates the drawing of all primitives registered in an ordering table.",
            "ClearOTag" to "<b>ClearOTag</b>: Clears an ordering table.",
            "VSync" to "<b>VSync</b>: Waits for the vertical retrace. Pass 0 to wait for the next retrace.",
            "DrawSync" to "<b>DrawSync</b>: Waits until the GPU has finished all current drawing commands.",
            "CdRead" to "<b>CdRead</b>: Reads sectors from the CD-ROM into memory.",
            "CdSearchFile" to "<b>CdSearchFile</b>: Searches for a file on the CD-ROM by name.",
            "SpuInit" to "<b>SpuInit</b>: Initializes the SPU (Sound Processing Unit).",
            "SpuSetVoiceAttr" to "<b>SpuSetVoiceAttr</b>: Sets attributes (pitch, volume, address) for an SPU voice.",
            "SsInit" to "<b>SsInit</b>: Initializes the sound system (Extended library).",
            "FntPrint" to "<b>FntPrint</b>: Adds a string to the internal print buffer for on-screen display.",
            "printf" to "<b>printf</b>: Standard C print function (redirected to SIO or on-screen via BIOS).",
            "EnterCriticalSection" to "<b>EnterCriticalSection</b>: Disables interrupts. Returns 1 if successful.",
            "ExitCriticalSection" to "<b>ExitCriticalSection</b>: Enables interrupts after a critical section.",
            "FixedMul" to "<b>FixedMul(a, b)</b>: Multiplies two 16.16 fixed-point numbers.",
            "rsin" to "<b>rsin(a)</b>: Returns the sine of an angle (0-4096 range) in 1.12 fixed-point format.",
            "rcos" to "<b>rcos(a)</b>: Returns the cosine of an angle (0-4096 range) in 1.12 fixed-point format.",
            "ratan2" to "<b>ratan2(y, x)</b>: Returns the arc tangent of y/x in 0-4096 range.",
            "_96_init" to "<b>_96_init</b>: Initializes the BIOS A0 table and kernel services.",
            "PadRead" to "<b>PadRead</b>: Reads the current state of the game controllers.",
            "MemCardInit" to "<b>MemCardInit</b>: Initializes the Memory Card system.",
            "MemCardOpen" to "<b>MemCardOpen</b>: Opens a file on the Memory Card.",
            "DecDCTin" to "<b>DecDCTin</b>: Feeds bitstream data to the MDEC for decoding.",
            "DecDCTout" to "<b>DecDCTout</b>: Retrieves decoded image data from the MDEC.",
            "InitTAP" to "<b>InitTAP</b>: Initializes the Multi-Tap peripheral support.",
            "GsHMDInit" to "<b>GsHMDInit</b>: Initializes an HMD model structure for rendering."
        )
    }
}
