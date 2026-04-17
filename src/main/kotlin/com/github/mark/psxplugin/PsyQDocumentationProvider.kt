package com.github.mark.psxplugin

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.psi.PsiElement
import com.intellij.openapi.diagnostic.Logger

class PsyQDocumentationProvider : AbstractDocumentationProvider() {
    private val LOG = Logger.getInstance(PsyQDocumentationProvider::class.java)

    init {
        LOG.info("[DEBUG_LOG] PsyQDocumentationProvider INSTANTIATED")
    }

    override fun getCustomDocumentationElement(editor: com.intellij.openapi.editor.Editor, file: com.intellij.psi.PsiFile, contextElement: PsiElement?, targetOffset: Int): PsiElement? {
        if (contextElement == null) return null
        val text = contextElement.text
        LOG.info("[DEBUG_LOG] PsyQ getCustomDocElement for: '$text', lang: ${contextElement.language}")
        
        // If it's a number or a likely PsyQ identifier, we want to handle it
        if (tryConvertNumber(text) != null || 
            functionDocs.containsKey(text) || 
            registerDocs.containsKey(text) || 
            constantDocs.containsKey(text) || 
            structDocs.containsKey(text) || 
            macroDocs.containsKey(text) ||
            mipsRegisterDocs.containsKey(text.removePrefix("$").lowercase())) {
            return contextElement
        }
        return null
    }

    override fun generateDoc(element: PsiElement?, originalElement: PsiElement?): String? {
        val target = originalElement ?: element ?: return null
        val text = target.text.trim()
        
        LOG.info("[DEBUG_LOG] PsyQ generateDoc called for text: '$text', element: $target")
        
        // 1. Handle Numbers
        tryConvertNumber(text)?.let { return it }

        // 2. Try exact match first
        val doc = functionDocs[text] ?: registerDocs[text] ?: constantDocs[text] ?: structDocs[text] ?: macroDocs[text]
        if (doc != null) {
            val link = "https://psx.arthus.net/sdk/Psy-Q/DOCS/Devrefs/Libovr.pdf"
            return "$doc<br><br><a href=\"$link\">Online Reference (PDF)</a>"
        }

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
            "setPolyF3" to "<b>setPolyF3(p)</b>: Helper macro to initialize a flat-shaded triangle primitive.",
            "CombSioStatus" to "<b>CombSioStatus()</b>: Returns the current status of the serial controller.",
            "CombReset" to "<b>CombReset()</b>: Resets the serial controller and clears the communication buffers.",
            "CombSetBPS" to "<b>CombSetBPS(bps)</b>: Sets the serial transfer rate (bits per second).",
            "CombSetMode" to "<b>CombSetMode(mode)</b>: Configures the serial communication mode (data bits, stop bits, parity)."
        )
        private val structDocs = mapOf(
            "RECT" to "<b>RECT</b>: Defines a rectangular area. Contains short x, y (top-left) and short w, h (width, height).",
            "RECT32" to "<b>RECT32</b>: Defines a rectangular area using 32-bit integers. Contains long x, y, w, h.",
            "SVECTOR" to "<b>SVECTOR</b>: Short vector (16-bit). Contains short vx, vy, vz and short pad.",
            "VECTOR" to "<b>VECTOR</b>: Long vector (32-bit). Contains long vx, vy, vz and long pad.",
            "CVECTOR" to "<b>CVECTOR</b>: Color vector (8-bit). Contains byte r, g, b, cd (code).",
            "DVECTOR" to "<b>DVECTOR</b>: 2D short vector. Contains short vx, vy.",
            "MATRIX" to "<b>MATRIX</b>: 3x3 rotation matrix and 3x1 translation vector. Used for GTE transformations.",
            "DRAWENV" to "<b>DRAWENV</b>: Drawing environment structure. Defines clipping area, background color, offset, etc.",
            "DISPENV" to "<b>DISPENV</b>: Display environment structure. Defines display area, screen offset, interlace mode, etc.",
            "POLY_F3" to "<b>POLY_F3</b>: Flat-shaded triangle primitive (no texture, single color).",
            "POLY_F4" to "<b>POLY_F4</b>: Flat-shaded quadrangle primitive (no texture, single color).",
            "POLY_FT3" to "<b>POLY_FT3</b>: Flat-shaded textured triangle primitive.",
            "POLY_FT4" to "<b>POLY_FT4</b>: Flat-shaded textured quadrangle primitive.",
            "POLY_G3" to "<b>POLY_G3</b>: Gouraud-shaded triangle primitive (interpolated colors per vertex).",
            "POLY_G4" to "<b>POLY_G4</b>: Gouraud-shaded quadrangle primitive (interpolated colors per vertex).",
            "POLY_GT3" to "<b>POLY_GT3</b>: Gouraud-shaded textured triangle primitive.",
            "POLY_GT4" to "<b>POLY_GT4</b>: Gouraud-shaded textured quadrangle primitive.",
            "LINE_F2" to "<b>LINE_F2</b>: Single-segment flat-shaded line primitive.",
            "LINE_G2" to "<b>LINE_G2</b>: Single-segment Gouraud-shaded line primitive.",
            "LINE_F3" to "<b>LINE_F3</b>: 2-segment (3-point) flat-shaded polyline primitive.",
            "LINE_G3" to "<b>LINE_G3</b>: 2-segment (3-point) Gouraud-shaded polyline primitive.",
            "LINE_F4" to "<b>LINE_F4</b>: 3-segment (4-point) flat-shaded polyline primitive.",
            "LINE_G4" to "<b>LINE_G4</b>: 3-segment (4-point) Gouraud-shaded polyline primitive.",
            "SPRT" to "<b>SPRT</b>: Free-size sprite primitive. Can be any width and height.",
            "SPRT_16" to "<b>SPRT_16</b>: Fixed-size 16x16 sprite primitive.",
            "SPRT_8" to "<b>SPRT_8</b>: Fixed-size 8x8 sprite primitive.",
            "TILE" to "<b>TILE</b>: Free-size flat-shaded rectangle (solid color).",
            "TILE_16" to "<b>TILE_16</b>: Fixed-size 16x16 flat-shaded rectangle.",
            "TILE_8" to "<b>TILE_8</b>: Fixed-size 8x8 flat-shaded rectangle.",
            "TILE_1" to "<b>TILE_1</b>: Fixed-size 1x1 dot primitive.",
            "GsOT" to "<b>GsOT</b>: Ordering Table structure for the high-level libgs library.",
            "GsOT_TAG" to "<b>GsOT_TAG</b>: Single entry in a libgs Ordering Table.",
            "GsDOBJ2" to "<b>GsDOBJ2</b>: 3D object handler structure for libgs.",
            "GsSPRITE" to "<b>GsSPRITE</b>: Sprite handler structure for libgs.",
            "GsBG" to "<b>GsBG</b>: Background handler structure for libgs.",
            "GsLINE" to "<b>GsLINE</b>: Line handler structure for libgs.",
            "GsBOXF" to "<b>GsBOXF</b>: Filled box handler structure for libgs.",
            "CdlFILE" to "<b>CdlFILE</b>: Structure containing file information (name, pos, size) for CD-ROM operations.",
            "TIM_IMAGE" to "<b>TIM_IMAGE</b>: Structure containing TIM image information (VRAM position, CLUT, etc.).",
            "DECDCTENV" to "<b>DECDCTENV</b>: Environment structure for MDEC decoding. Contains quantization tables and IDCT coefficients.",
            "ENCSPUENV" to "<b>ENCSPUENV</b>: Environment structure for SPU ADPCM encoding. Defines source PCM data, destination buffer, and quality settings."
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
            "GsSwapDisplay" to "<b>GsSwapDisplay</b>: Alias for GsSwapDispBuff. Swaps the drawing and display buffers (Double buffering).",
            "GsSwapDispBuff" to "<b>GsSwapDispBuff</b>: Swaps the drawing and display buffers (Double buffering).",
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
            "GsHMDInit" to "<b>GsHMDInit</b>: Initializes an HMD model structure for rendering.",
            "sin" to "<b>sin(a)</b>: Returns the sine of an angle (standard C math library).",
            "cos" to "<b>cos(a)</b>: Returns the cosine of an angle (standard C math library).",
            "tan" to "<b>tan(a)</b>: Returns the tangent of an angle (standard C math library).",
            "sqrt" to "<b>sqrt(a)</b>: Returns the square root of a number (standard C math library).",
            "pow" to "<b>pow(base, exp)</b>: Returns the value of base raised to the power of exp.",
            "DecDCTReset" to "<b>DecDCTReset(mode)</b>: Resets the MDEC (Motion Decoder) to its initial state.",
            "EncSPU" to "<b>EncSPU(env)</b>: Encodes PCM audio data into PlayStation-standard ADPCM format.",
            "DsInit" to "<b>DsInit()</b>: Initializes the high-level CD-ROM streaming library.",
            "DsCommand" to "<b>DsCommand(com, param, cbsync, count)</b>: Sends a command to the CD-ROM drive using the high-level DS library.",
            "AddCOMB" to "<b>AddCOMB()</b>: Installs the serial communication (Link Cable) interrupt handler.",
            "DelCOMB" to "<b>DelCOMB()</b>: Uninstalls the serial communication (Link Cable) interrupt handler.",
            "InitGUN" to "<b>InitGUN(...)</b>: Initializes the Light Gun library and sets up the communication buffers.",
            "StartGUN" to "<b>StartGUN()</b>: Starts the scanning process for the Light Gun.",
            "StopGUN" to "<b>StopGUN()</b>: Stops the Light Gun scanning process.",
            "EnableTAP" to "<b>EnableTAP()</b>: Enables the Multi-Tap adapter interface.",
            "DisableTAP" to "<b>DisableTAP()</b>: Disables the Multi-Tap adapter interface.",
            "MemCardStart" to "<b>MemCardStart()</b>: Starts the Memory Card interrupt handler and communication system.",
            "MemCardStop" to "<b>MemCardStop()</b>: Stops the Memory Card system and releases resources.",
            "MemCardExist" to "<b>MemCardExist(chan)</b>: Checks if a Memory Card is physically inserted in the specified slot.",
            "MemCardAccept" to "<b>MemCardAccept(chan)</b>: Attempts to identify and 'accept' the Memory Card in the specified slot.",
            "MemCardSync" to "<b>MemCardSync(mode, cmds, rslt)</b>: Waits for the current Memory Card operation to complete."
        )
    }
}
